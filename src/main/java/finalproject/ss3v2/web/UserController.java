package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.RefreshToken;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserService;
import finalproject.ss3v2.service.UserServiceImpl;
import finalproject.ss3v2.util.CookieUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {
    private UserServiceImpl userServiceImpl;
    private RefreshTokenService refreshTokenService;



    public UserController(UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService) {
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
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
            return "usersession";
        }
        if(refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId()).equals(false)){
            model.addAttribute("tokenexpired", true);
        }
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
        if(refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId()).equals(false)){
            model.addAttribute("tokenexpired", true);
        }
        return "redirect:/signin";
    }

    @PostMapping("/usersession/{userId}/edituser")
    public String updateUser(User user, Authentication authentication, Model model) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            userServiceImpl.save(user);
            return "redirect:/usersession/" + user.getId();
        }
        if(refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId()).equals(false)){
            model.addAttribute("tokenexpired", true);
        }
        return "redirect:/signin";

    }
}
