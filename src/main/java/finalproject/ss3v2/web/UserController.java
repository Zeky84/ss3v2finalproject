package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.UserService;
import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    UserService userService;
    UserServiceImpl userServiceImpl;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    // Among corrections in the enpoints directions the code  below was provided fully by CHATGPT
    @GetMapping("/usersession")
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }
        return "redirect:/login"; // Redirect to login if not authenticated
    }

    @GetMapping("/usersession/{userId}")
    public String userSession(@PathVariable Long userId, Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            return "usersession"; // HTML view for the user's session
        }
        return "redirect:/login"; // Redirect to login if not authenticated
    }
    //------------------------------------------------------------------------------
}
