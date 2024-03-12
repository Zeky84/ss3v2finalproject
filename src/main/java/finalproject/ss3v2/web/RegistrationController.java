package finalproject.ss3v2.web;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import finalproject.ss3v2.dao.request.SignUpRequest;
import finalproject.ss3v2.dao.response.JwtAuthenticationResponse;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.security.AuthenticationServiceImpl;
import finalproject.ss3v2.security.JwtServiceImpl;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserServiceImpl;

@Controller
public class RegistrationController {

    private UserServiceImpl userService;
    private AuthenticationServiceImpl authenticationService;
    private JwtServiceImpl jwtService;
    private RefreshTokenService refreshTokenService;
    private PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(RegistrationController.class);


    public RegistrationController(UserServiceImpl userService, AuthenticationServiceImpl authenticationService,
                                  JwtServiceImpl jwtService, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder) {
        super();
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/registration")
    public String getRegistration (ModelMap model) {
        model.addAttribute("user", new User());
        return "registration";
    }


    @PostMapping("/registration")// this method logic was change from the original
    public String processRegistration(@ModelAttribute("user") User user, SignUpRequest request, Model model) {
        Optional<User> existingUser = userService.findUserByEmail(user.getEmail());
        String encodedPassword = passwordEncoder.encode(request.password());


        if (existingUser.isEmpty()) {
            JwtAuthenticationResponse signupResponse = authenticationService.signup(request);

            logger.info("Processing registration for user: " + user.getEmail());
            logger.info("Provided password during registration: " + request.password());
            logger.info("Encoded password during registration: " + encodedPassword);

            if (signupResponse != null) {
                // Successfully registered user, now proceed with authentication
                logger.info("Successfully registered user. Redirecting to success.");
                return "redirect:/signin";
            } else {
                // Handle the case where authentication is not successful
                logger.error("User registration failed. Redirecting to error.");
                return "redirect:/error";
            }

        } else {
            logger.error("User already exists.");
            // Redirect to the userExists page if a user with the same email exists
            model.addAttribute("userExists", true);
            model.addAttribute("user", new User());
            return "registration";
        }

    }
}
