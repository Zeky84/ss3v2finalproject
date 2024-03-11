package finalproject.ss3v2.web;


import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;

import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public String goToUserSession(Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("isSessionActive", true); // Session is active
            return "usersession";
        }
//        if(refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId()).equals(false)){
//            model.addAttribute("tokenexpired", true);
//        }
        return "redirect:/signin";
    }

    @GetMapping("/usersession/{userId}/edituser")
    public String goToEditUser(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            //Using the user id instead of the principal of the authentication to get the user object to update the frontend, otherwise,
            //when refreshing the page the user fields will be filled with the old data all the time
            User user = userServiceImpl.findUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            model.addAttribute("user", user);
            return "edituser";
        }
//        if(refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId()).equals(false)){
//            model.addAttribute("tokenexpired", true);
//        }
        return "redirect:/signin";
    }

    @PostMapping("/usersession/{userId}/edituser")
    public String updateUser(User userFields, @RequestParam(required = false) String newPassword, Authentication authentication, Model model) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User existingUser = userServiceImpl.findUserById(userFields.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userFields.getId()));

            // Update user details
            existingUser.setEmail(userFields.getEmail());
            existingUser.setFirstName(userFields.getFirstName());
            existingUser.setLastName(userFields.getLastName());

            // If a new password is provided, encode and update it
            if (newPassword != null && !newPassword.isBlank()) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                existingUser.setPassword(encodedPassword);
            }

            userServiceImpl.save(existingUser);
            return "redirect:/usersession/" + userFields.getId();
        }
        return "redirect:/signin";
    }
}
