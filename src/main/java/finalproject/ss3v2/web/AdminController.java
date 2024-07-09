package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.Authority;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.UserServiceImpl;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImpl userServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        super();
        this.userServiceImpl = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct // This annotation is used to create an admin user during application startup
    public void init() {
        createAdminUser();
    }



    public void createAdminUser() {// This method is used to create an admin user during application startup, if admin already created,
        // then it will not create again, this is to avoid creating multiple admin users when deploying the application the app and updating the database
        if(userServiceImpl.findUserByEmail("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            Authority adminAuth = new Authority("ROLE_ADMIN", adminUser);
            adminUser.setAuthorities(Collections.singletonList(adminAuth));
            adminUser.setAdmin(true);// added to the original code
            adminUser.setUser(true);// added to the original code
            userServiceImpl.save(adminUser);
        }

    }

    @GetMapping("/dashboard")
    public String getDashboard(ModelMap model, Authentication authentication) {
        String adminname = authentication.getName();
        List<User> users = userServiceImpl.findAll();
        model.addAttribute("userList", users);
        model.addAttribute("adminname", adminname);
        return "dashboard";
    }

//    @PostMapping("/makeAdmin")--------------------------------------
//    public ResponseEntity<String> elevateToAdmin(@RequestParam Integer userId) {
//        Optional<User> findUser = userServiceImpl.findUserById(userId);
//        userServiceImpl.elevateUserToAdmin(userId);
//        logger.info("Processing elevation for user: {}", findUser.get().getEmail());
//        logger.info("Role: {}", findUser.get().getAuthorities());
//        return ResponseEntity.ok("User elevated to admin");
//    }-------------------------------------------------------------

    //------------------------------------------------------------------adding to the original code
    @PostMapping("/makeAdmin")
    public String elevateToAdmin(@RequestParam Integer userId) {
        Optional<User> findUser = userServiceImpl.findUserById(userId);
        userServiceImpl.elevateUserToAdmin(userId);
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/removeAdmin")
    public String removeAdminPrivileges(@RequestParam Integer userId) {
        Optional<User> findUser = userServiceImpl.findUserById(userId);
        userServiceImpl.removeAdminPrivileges(userId);
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/makeSuperUser")
    public String elevateToSuperUser(@RequestParam Integer userId) {
        Optional<User> findUser = userServiceImpl.findUserById(userId);
        userServiceImpl.elevateUserToSuperUser(userId);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/removeSuperUser")
    public String removeSuperUserPrivileges(@RequestParam Integer userId) {
        Optional<User> findUser = userServiceImpl.findUserById(userId);
        userServiceImpl.removeSuperUserPrivileges(userId);
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam Integer userId) {
//        Optional<User> findUser = userServiceImpl.findUserById(userId);
        userServiceImpl.deleteUser(userId);
        return "redirect:/admin/dashboard";
    }
}
