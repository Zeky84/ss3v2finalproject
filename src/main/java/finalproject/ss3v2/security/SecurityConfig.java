package finalproject.ss3v2.security;

import finalproject.ss3v2.domain.RefreshToken;
import finalproject.ss3v2.domain.Role;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserServiceImpl;
import finalproject.ss3v2.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final RefreshTokenService refreshTokenService;
    private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserServiceImpl userService,
                          JwtServiceImpl jwtService, RefreshTokenService refreshTokenService) {
        super();
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
//        .authorizeHttpRequests(request -> request.requestMatchers("**").permitAll().anyRequest().authenticated())

                //---------------------------------------------------------------------added to the original
                // Redirecting to denied page for unauthorized access user and unauthenticated users
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Redirect to error for unauthenticated access
                            response.sendRedirect("/error?unAuthenticated=true"); // redirect to error page(no controller)
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Redirect to error for unauthorized access
                            response.sendRedirect("/error?unAuthorized=true"); // redirect to error page(no controller)
                        })
                )
                //-------------------------------------------------------------------------------------------
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/usersession/**").hasAnyRole(Role.ADMIN.name(), Role.USER.name(), Role.SUPERUSER.name())
                        .requestMatchers("/edituser").hasAnyRole(Role.ADMIN.name(), Role.USER.name(), Role.SUPERUSER.name())
                        .requestMatchers("/success").authenticated()
                        .requestMatchers("homepage", "/registration","/api/**","/app-testing/**").permitAll()
                        .anyRequest().permitAll()
                )
                .headers(header -> header.frameOptions(frameOption -> frameOption.disable()))
//                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> {
                    login
                            .loginPage("/signin")
//		        	.failureUrl("/failure"); // this can be linked to a failure message on the failure template
                            .usernameParameter("email")
                            .successHandler(new AuthenticationSuccessHandler() {

                                @Override
                                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                                    Authentication authentication) throws IOException, ServletException {

                                    //HttpServletResponseWrapper ensures that the cookie is set only when the authentication is successful
                                    response = new HttpServletResponseWrapper(response);
                                    User user = (User) authentication.getPrincipal();
                                    String accessToken = jwtService.generateToken(new HashMap<>(), user);
                                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

                                    Cookie accessTokenCookie = CookieUtils.createAccessTokenCookie(accessToken);
                                    Cookie refreshTokenCookie = CookieUtils.createRefreshTokenCookie(refreshToken.getToken());

                                    logger.info("Successful authentication for: " + user.getUsername());
                                    logger.info("Access Cookie: " + accessTokenCookie.getValue());
                                    logger.info("Refresh Cookie: " + refreshTokenCookie.getValue());
                                    logger.info("Role for " + user.getUsername() + " is: " + user.authority(accessToken).getAuthorities().toString());
                                    logger.info("Successful authentication for: " + user.getUsername());
                                    logger.info("Access Cookie: " + accessTokenCookie.getValue());
                                    logger.info("Refresh Cookie: " + refreshTokenCookie.getValue());
                                    logger.info("Role for " + user.getUsername() + " is: " + user.authority(accessToken).toString());

                                    response.addCookie(accessTokenCookie);
                                    response.addCookie(refreshTokenCookie);


                                response.sendRedirect("/usersession");
                                }
                            })
                            .failureHandler(new AuthenticationFailureHandler() {

                                @Override
                                public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                                    AuthenticationException exception) throws IOException, ServletException {

                                    String email = request.getParameter("email");
                                    String password = request.getParameter("password");

                                    logger.error("Authentication failed for email: " + email);
                                    logger.error("Authentication failed: " + exception.getMessage(), exception);
                                    logger.info("Raw password during login: " + password);
                                    logger.info("Encoded password during login: " + passwordEncoder().encode(password));

                                    response.sendRedirect("/signin?authError");// /error before
                                }
                            })
                            .permitAll();
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/homepage")
                            // delete cookies from client after logout
                            .deleteCookies("accessToken")
                            .deleteCookies("refreshToken")
                            .deleteCookies("JSESSIONID")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true);
                });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        UserDetailsService userDetailsService = userService.userDetailsService();
        PasswordEncoder passwordEncoder = passwordEncoder();

        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        logger.info("UserDetailsService: " + userDetailsService);
        logger.info("PasswordEncoder: " + passwordEncoder);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
