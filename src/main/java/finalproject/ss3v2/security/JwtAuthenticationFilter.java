package finalproject.ss3v2.security;

import java.io.IOException;

import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import finalproject.ss3v2.dao.request.RefreshTokenRequest;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserServiceImpl;
import finalproject.ss3v2.util.CookieUtils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final JwtServiceImpl jwtService;
    private final UserServiceImpl userService;
    private final RefreshTokenService refreshTokenService;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtServiceImpl jwtService, UserServiceImpl userService,
                                   RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        Cookie accessTokenCookie = getCookie(request, ACCESS_TOKEN_COOKIE_NAME);
        Cookie refreshTokenCookie = getCookie(request, REFRESH_TOKEN_COOKIE_NAME);

        if (accessTokenCookie != null) {
            processAccessToken(accessTokenCookie, refreshTokenCookie, response);
        } else {
            logger.error("Access token is missing.");
        }

        logger.debug("Request URI: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private void processAccessToken(Cookie accessTokenCookie, Cookie refreshTokenCookie, HttpServletResponse response) {
        int loginAttempt = 0;

        while (loginAttempt <= MAX_LOGIN_ATTEMPTS) {
            String token = accessTokenCookie.getValue();
            try {
                String subject = jwtService.extractUserName(token);
                if (StringUtils.hasText(subject) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.userDetailsService().loadUserByUsername(subject);

                    if (jwtService.isTokenValid(token, userDetails)) {
                        setAuthentication(userDetails);
                        break;
                    }
                }
            } catch (ExpiredJwtException e) {
                if (refreshTokenCookie != null) {
                    token = refreshAccessToken(refreshTokenCookie, response);
                    if (token == null) {
                        break;
                    }
                    accessTokenCookie = CookieUtils.createAccessTokenCookie(token);
                    response.addCookie(accessTokenCookie);
                } else {
                    logger.error("Refresh token is missing.");
                    break;
                }
            }
            loginAttempt++;
        }
    }

    private void setAuthentication(UserDetails userDetails) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    private String refreshAccessToken(Cookie refreshTokenCookie, HttpServletResponse response) {
        try {
            return refreshTokenService.createNewAccessToken(new RefreshTokenRequest(refreshTokenCookie.getValue()));
        } catch (Exception e) {
            logger.error("Failed to refresh access token", e);
            return null;
        }
    }
}

