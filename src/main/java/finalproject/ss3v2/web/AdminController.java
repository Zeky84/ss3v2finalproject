package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.Authority;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.repository.UserRepository;
import finalproject.ss3v2.service.AuthorityService;
import finalproject.ss3v2.service.UserServiceImpl;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//@RestController
@Controller
@RequestMapping("/admin")
public class AdminController {
    private UserServiceImpl userService;
    private AuthorityService authorityService;
    private PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        super();
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct // This annotation is used to create an admin user during application startup
    public void init() {
        createAdminUser();
    }

    List<User> allAdmins = new ArrayList<>();


    public void createAdminUser() {
        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));

        Authority adminAuth = new Authority("ROLE_ADMIN", adminUser);
        adminUser.setAuthorities(Collections.singletonList(adminAuth));
        adminUser.setAdmin(true);
        userService.save(adminUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/dashboard")
    public String getDashboard(ModelMap model) {
        List<User> users = userService.findAll();
        model.addAttribute("userList", users);
        return "dashboard";
    }

    @PostMapping("/makeAdmin")
    public ResponseEntity<String> elevateToAdmin(@RequestParam Integer userId) {
        Optional<User> findUser = userService.findUserById(userId);
        userService.elevateUserToAdmin(userId);
        logger.info("Processing elevation for user: {}", findUser.get().getEmail());
        logger.info("Role: {}", findUser.get().getAuthorities());
        return ResponseEntity.ok("User elevated to admin");
    }

    @PostMapping("/removeAdmin")
    public ResponseEntity<String> removeAdmin(@RequestParam Integer userId) {
        Optional<User> findUser = userService.findUserById(userId);
        userService.removeAdminPrivileges(userId);
        logger.info("Removing Admin for user: {}", findUser.get().getEmail());
        logger.info("Role: {}", findUser.get().getAuthorities());
        return ResponseEntity.ok("Removing admin privileges for user");
    }
    @PostMapping("/makeSuperUser")
    public ResponseEntity<String> elevateToSuperUser(@RequestParam Integer userId) {
        Optional<User> findUser = userService.findUserById(userId);
        userService.elevateUserToSuperUser(userId);
        logger.info("Processing elevation for user: {}", findUser.get().getEmail());
        logger.info("Role: {}", findUser.get().getAuthorities());
        return ResponseEntity.ok("User elevated to admin");
    }

    @PostMapping("/removeSuperUser")
    public ResponseEntity<String> removeSuperUser(@RequestParam Integer userId) {
        Optional<User> findUser = userService.findUserById(userId);
        userService.removeSuperUserPrivileges(userId);
        logger.info("Removing Admin for user: {}", findUser.get().getEmail());
        logger.info("Role: {}", findUser.get().getAuthorities());
        return ResponseEntity.ok("Removing admin privileges for user");
    }
}
