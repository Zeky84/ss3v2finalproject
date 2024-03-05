package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserService;
import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/usersession")
public class UserController {
    @Value("${token.refreshExpiration}")
    private Integer expirtationtimeInMinutes;
    private UserServiceImpl userServiceImpl;
    private RefreshTokenService refreshTokenService;


    public UserController( UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService) {
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpiration(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }
        return "redirect:/error"; // Redirect to error page if not authenticated or token expired
    }

    @GetMapping("/{userId}")
    public String goToUserSession(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpiration(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            return "usersession";
        }
        return "redirect:/error"; // Redirect to error page if not authenticated or token expired
    }

    @GetMapping("/{userId}/edituser")
    public String goToEditUser(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpiration(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            //Using the user id instead of the principal of the authentication to get the user object to update the frontend, otherwise,
            //when refreshing the page the user fields will be filled with the old data all the time
            User user = userServiceImpl.findUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            model.addAttribute("user", user);
            return "edituser";
        }
        return "redirect:/error"; // Redirect to login if not authenticated
    }

    @PostMapping("/{userId}/edituser")
    public String updateUser(User user) {

        userServiceImpl.save(user);
        return "redirect:/usersession/" + user.getId();
    }
    @GetMapping("/newrefreshtoken")
    @ResponseBody
    public String createNewRefreshToken(Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            return "Refresh token created";
        }
        return "Error creating refresh token";
    }

}
