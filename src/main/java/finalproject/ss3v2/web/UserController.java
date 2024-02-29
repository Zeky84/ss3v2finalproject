package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserService;
import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Value("${token.refreshExpiration}")
    private Integer expirtationtimeInMinutes;

    private UserService userService;
    private UserServiceImpl userServiceImpl;
    private RefreshTokenService refreshTokenService;


    public UserController(UserService userService, UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/usersession")
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpiration(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }
        return "redirect:/error"; // Redirect to error page if not authenticated or token expired
    }

    @GetMapping("/usersession/{userId}")
    public String userSession(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpiration(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            return "usersession";
        }
        return "redirect:/error"; // Redirect to error page if not authenticated or token expired
    }

    @GetMapping("/edituser")
    public String editUser(Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpiration(((User) authentication.getPrincipal()).getId())) {
            refreshTokenService.createRefreshToken(((User) authentication.getPrincipal()).getId());
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            return "edituser";
        }
        return "redirect:/homepage"; // Redirect to login if not authenticated
    }

    @PostMapping("/edituser")
    public String edituser(User user) {
        Integer userId = user.getId();
        userServiceImpl.updateUser(user);
        return "redirect:/usersession";
    }

}
