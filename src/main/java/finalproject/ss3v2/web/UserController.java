package finalproject.ss3v2.web;


import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;

import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Controller
public class UserController {
    private UserServiceImpl userServiceImpl;
    private RefreshTokenService refreshTokenService;
    private PasswordEncoder passwordEncoder;


    public UserController(UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping("/usersession")
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null) {
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }

        return "redirect:/error";
    }

    @GetMapping("/usersession/{userId}")
    public String goToUserSession(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);
            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/usersession/{userId}/edituser")
    public String goToEditUser(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);
            return "edituser";
        }
        return "redirect:/signin";
    }

    @PostMapping("/usersession/{userId}/edituser")
    public String updateUser(@PathVariable Integer userId, User userFields,
                             @RequestParam(required = false) String newPassword,
                             Authentication authentication, Model model) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {

            User authenticatedUser = (User) authentication.getPrincipal();

            // Check if the authenticated user's ID matches the user ID from the URL to avoid unauthorized updates
            if (!authenticatedUser.getId().equals(userId)) {
                // Redirect to an error page or a 'forbidden' page
                return "redirect:/error"; //todo: make a nice desing view for this like the one pending for unauthorized and unauthenticated in the security config
            }

            User existingUser = userServiceImpl.findUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            // Update user details
            existingUser.setEmail(userFields.getEmail());
            existingUser.setFirstName(userFields.getFirstName());
            existingUser.setLastName(userFields.getLastName());

            if (newPassword != null && !newPassword.isBlank()) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                existingUser.setPassword(encodedPassword);
            }

            userServiceImpl.save(existingUser);

            // Update the security context
            Authentication newAuth = new UsernamePasswordAuthenticationToken(existingUser, existingUser.getPassword(), existingUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return "redirect:/usersession/" + userId;
        }
        return "redirect:/signin";
    }
}
