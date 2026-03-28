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
    private final JwtServiceImpl jwtService;
    private final UserServiceImpl userService;
    private final RefreshTokenService refreshTokenService;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtServiceImpl jwtService, UserServiceImpl userService,
                                   RefreshTokenService refreshTokenService) {
        super();
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        Cookie accessTokenCookie = null;
        Cookie refreshTokenCookie = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("accessToken")) {
                    accessTokenCookie = cookie;
                } else if (cookie.getName().equals("refreshToken")) {
                    refreshTokenCookie = cookie;
                }
            }
        }

        if (accessTokenCookie != null) {

            String token = accessTokenCookie.getValue();

            try {
                String subject = jwtService.extractUserName(token);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (StringUtils.hasText(subject) && authentication == null) {
                    UserDetails userDetails = userService.userDetailsService().loadUserByUsername(subject);

                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

            } catch (ExpiredJwtException e) {

                // 🔥 Only try refresh if refresh token exists
                if (refreshTokenCookie != null) {
                    try {
                        String newAccessToken = refreshTokenService
                                .createNewAccessToken(new RefreshTokenRequest(refreshTokenCookie.getValue()));

                        if (newAccessToken != null) {
                            Cookie newCookie = CookieUtils.createAccessTokenCookie(newAccessToken);
                            response.addCookie(newCookie);
                        }

                    } catch (Exception ex) {
                        // ✅ Do NOT spam logs, just clear context
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        }
        logger.debug("Request URI: {}", request.getRequestURI());
        logger.debug("Auth Header: {}", authHeader);
        logger.debug("Access Token Cookie: {}", accessTokenCookie);
        logger.debug("Refresh Token Cookie: {}", refreshTokenCookie);

        filterChain.doFilter(request, response);

    }
}
