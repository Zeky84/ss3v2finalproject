package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.UserService;
import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    UserService userService;
    UserServiceImpl userServiceImpl;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    // the code  below was provided fully by CHATGPT.
    @GetMapping("/usersession")
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null) {
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }
        return "redirect:/login"; // Redirect to login if not authenticated
    }

    @GetMapping("/usersession/{userId}")
    public String userSession(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            return "usersession"; // HTML view for the user's session
        }
        return "redirect:/login"; // Redirect to login if not authenticated
    }
    @GetMapping("/edituser")
        public String editUser(Model model, Authentication authentication) {
            if (authentication != null) {
                User user = (User) authentication.getPrincipal();
                model.addAttribute("user", user);
                return "edituser"; // HTML view for editing user details
            }
            return "redirect:/login"; // Redirect to login if not authenticated
        }
        @PostMapping("/edituser")
        public String edituser(User user, Authentication authentication) {
            if (authentication != null) {
                userServiceImpl.updateUser(user);
                return "redirect:/usersession"; // Redirect to user session after editing
            }
            return "redirect:/login"; // Redirect to login if not authenticated
        }

}
