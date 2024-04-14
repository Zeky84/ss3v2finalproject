package finalproject.ss3v2.web;


import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.ApiServiceHudUser;
import finalproject.ss3v2.service.RefreshTokenService;

import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/usersession")
public class UserController {
    private UserServiceImpl userServiceImpl;
    private RefreshTokenService refreshTokenService;
    private PasswordEncoder passwordEncoder;

    private ApiServiceHudUser apiServiceHudUser;



    public UserController(UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder,
                          ApiServiceHudUser apiServiceHudUser) {
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.apiServiceHudUser = apiServiceHudUser;
    }

    @GetMapping("")
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null) {
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }
        return "redirect:/error";
    }

    @GetMapping("/{userId}")
    public String goToUserSession(@PathVariable Integer userId, Model model, ModelMap modelMap, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            // the user from the auth object instead from the to avoid any possible manipulation of the URL
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            // Addingg the states and metro areas to the html view
            modelMap.put("states", apiServiceHudUser.getStatesList());
            modelMap.put("metroAreas", apiServiceHudUser.getMetroAreasList());

            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/metroarea/data/{metroAreaCode}")
    public String getDataByMetroArea(@PathVariable Integer userId, @PathVariable String metroAreaCode, Model model, Authentication authentication){
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(metroAreaCode));

            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/counties/{stateCode}")
    public String getCountiesByState(@PathVariable Integer userId, @PathVariable String stateCode, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
            model.addAttribute("stateCode", stateCode);
            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/counties/{stateCode}/data/{countyCode}")
    public String getDataByZipCode(@PathVariable Integer userId, @PathVariable String stateCode,
                                   @PathVariable String countyCode, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(countyCode));
            model.addAttribute("stateCode", stateCode);
            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/edituser")
    public String goToEditUser(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);
            return "edituser";
        }
        return "redirect:/signin";
    }

    @PostMapping("/{userId}/edituser")
    public String updateUser(@PathVariable Integer userId, User userFields,
                             @RequestParam(required = false) String newPassword,
                             Authentication authentication, Model model) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User authenticatedUser = (User) authentication.getPrincipal();

            // Check if the authenticated user's ID matches the user ID from the URL to avoid unauthorized updates. just in case
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

//    @GetMapping("/usersession/{userId}/{metroAreaCode}/zipcodes")
//    public String getZipCodesByMetroArea(@PathVariable Integer userId, @PathVariable String metroAreaCode, Model model, Authentication authentication) {
//        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
//            User userAuth = (User) authentication.getPrincipal();
//            model.addAttribute("user", userAuth);
//
//            model.addAttribute("zipCodes", apisService.getZipCodesByMetroArea(metroAreaCode));
//
//            return "zipcodes";
//        }
//        return "redirect:/signin";
//    }
}
