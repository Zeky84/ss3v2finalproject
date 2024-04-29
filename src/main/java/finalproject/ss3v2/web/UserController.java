package finalproject.ss3v2.web;


import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.dto.BasicData;
import finalproject.ss3v2.dto.ElectResponseData;

import finalproject.ss3v2.service.ApiServiceEnergyInfoAdmin;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/usersession")
public class UserController {
    private UserServiceImpl userServiceImpl;
    private RefreshTokenService refreshTokenService;
    private PasswordEncoder passwordEncoder;

    private ApiServiceHudUser apiServiceHudUser;

    private ApiServiceEnergyInfoAdmin apiServiceEnergyInfoAdmin;


    public UserController(UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder,
                          ApiServiceHudUser apiServiceHudUser, ApiServiceEnergyInfoAdmin apiServiceEnergyInfoAdmin) {
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.apiServiceHudUser = apiServiceHudUser;
        this.apiServiceEnergyInfoAdmin = apiServiceEnergyInfoAdmin;
    }

    @GetMapping("")
    public String redirectToUserSession(Authentication authentication) {
        if (authentication != null) {
            Integer userId = ((User) authentication.getPrincipal()).getId();
            return "redirect:/usersession/" + userId;
        }
        return "redirect:/error";
    }

    @GetMapping("/{userId}") // END-POINT FOR STARTING SEARCHING CRITERIA, SEARCH BY STATE OR METRO AREA
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

    @GetMapping("/{userId}/metroarea/data/{dataEntityCode}") // ENDPOINT METRO AREA SEARCHING CRITERIA
    public String getDataByMetroAreaCode(@PathVariable Integer userId, @PathVariable String dataEntityCode, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("entityCode", dataEntityCode);
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));

            // Metro Data does not offer an State code cause some metro areas are in many states at the same time.
            // and state code is need it to call to the EIA API and get  the elect rates by state. So we need to obtain
            // the state or states codes from metro_name.
            model.addAttribute("stateCodes", apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode));

            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/metroarea/data/{dataEntityCode}/dataid/{id}")// ENDPOINT MET AREA SEARCH CRITERIA FINAL DATA
    public String getSpecificDataByMetroAreaCode(@PathVariable Integer userId, @PathVariable String dataEntityCode, Model model,
                                                 @PathVariable Integer id, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));
            model.addAttribute("entityCode", dataEntityCode);
            model.addAttribute("stateCodes", apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode));

            // To manage the case when the metro area has more than one state code, so the user can select the state
            List<String> stateCodes = apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode);
            Map<String, Double> electRates = new HashMap<>();
            for (String stateCode : stateCodes) {
                electRates.put(stateCode, apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
            }

            // to manage when the data has more than one basic data object(the one that contains the rent values)
            List<BasicData> basicData = apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getBasicdata();
            if (basicData.size() > 1) {
                model.addAttribute("rentValues", basicData.get(id));
                model.addAttribute("electRates", electRates);
                model.addAttribute("stateCode", stateCodes);
                model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName() + " / Zip Code: " + basicData.get(id).getZipCode());
            }
            if (basicData.size() == 1) {
                model.addAttribute("rentValues", basicData.get(0));
                model.addAttribute("electRates", electRates);
                model.addAttribute("stateCode", stateCodes);
                model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getMetroName());

            }
            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/counties/{stateCode}") // EN-POINT FOR STATE-COUNTY SEARCHING CRITERIA
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

    @GetMapping("/{userId}/counties/{stateCode}/data/{dataEntityCode}") // EN-POINT FOR STATE-COUNTY SEARCHING CRITERIA
    public String getDataByZipCode(@PathVariable Integer userId, @PathVariable String stateCode,
                                   @PathVariable String dataEntityCode, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);


            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));
            model.addAttribute("stateCode", stateCode);
            model.addAttribute("entityCode", dataEntityCode);


            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/counties/{stateCode}/data/{dataEntityCode}/dataid/{id}")
    // EN-POINT METRO AREA SEARCHING CRITERIA FINAL DATA
    public String getSpecificDataByCountyCode(@PathVariable Integer userId, @PathVariable String stateCode, @PathVariable String dataEntityCode,
                                              Model model, @PathVariable Integer id, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);


            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));
            model.addAttribute("stateCode", stateCode);
            model.addAttribute("entityCode", dataEntityCode);

            List<BasicData> basicData = apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getBasicdata();
            if (basicData.size() > 1) {
                model.addAttribute("rentValues", basicData.get(id));
                model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName() + " / Zip Code: " + basicData.get(id).getZipCode());
            }
            if (basicData.size() == 1) {
                model.addAttribute("rentValues", basicData.get(0));
                model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getCountyName());
            }
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
