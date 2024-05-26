package finalproject.ss3v2.web;


import finalproject.ss3v2.domain.Profile;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.domain.Utilities;
import finalproject.ss3v2.dto.BasicData;

import finalproject.ss3v2.repository.UtilitiesRepository;
import finalproject.ss3v2.service.*;

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

    private ApiServiceZipCodeStack apiServiceZipCodeStack;

    private UtilitiesRepository utilitiesRepository;

    private ProfileService profileService;


    public UserController(UserServiceImpl userServiceImpl, RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder,
                          ApiServiceHudUser apiServiceHudUser, ApiServiceEnergyInfoAdmin apiServiceEnergyInfoAdmin,
                          UtilitiesRepository utilitiesRepository, ApiServiceZipCodeStack apiServiceZipCodeStack,
                          ProfileService profileService) {
        this.userServiceImpl = userServiceImpl;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.apiServiceHudUser = apiServiceHudUser;
        this.apiServiceEnergyInfoAdmin = apiServiceEnergyInfoAdmin;
        this.apiServiceZipCodeStack = apiServiceZipCodeStack;
        this.utilitiesRepository = utilitiesRepository;
        this.profileService = profileService;
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

            // Adding the states and metro areas to the html view
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
            if (apiServiceHudUser.getTheDataCostByCode(dataEntityCode) != null) {
                model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));
            } else {
                //found a case where no data was found... for example Arecibo, PR
                model.addAttribute("nodata", "no data found for this search");
            }
            // Metro Data does not offer a State code cause some metro areas are associated with more than one state
            // and state code is need it to call to the EIA API and get  the elect rates by state. So we need to obtain
            // the state or states codes from metro_name.(CHANGE THIS FOR THE ZIP CODE STACK API when zip code is available)
//            model.addAttribute("stateCodes", apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode));
            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/metroarea/data/{dataEntityCode}/dataid/{dataindex}")
// ENDPOINT METroAREA SEARCH CRITERIA FINAL DATA
    public String getSpecificDataByMetroAreaCode(@PathVariable Integer userId, @PathVariable String dataEntityCode, Model model,
                                                 @PathVariable Integer dataindex, Authentication authentication) {
        String stateCode;
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            User user = userServiceImpl.findUserById(userId).get();// we don't want to use the user from the security context
            // we used the user from the security context to make sure when accessing the view and editing the fields to
            // avoid any possible manipulation of the URL. But when creating the profile we need to use the user from the db

            //to manage quantities(gallons of fuel and persons) values need it to calculate fuel and elect costs
            Integer gallonsOfFuel = 0;
            Integer persons = 0;

            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("entityCode", dataEntityCode);
            model.addAttribute("stateCodes", apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode));
            model.addAttribute("dataindex", dataindex);

            model.addAttribute("gallonsOfFuel", gallonsOfFuel);
            model.addAttribute("personsLivingIn", persons);


            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));

            //Creating utilities profile, so the user will add values later to analyze and get total cost.
            Profile profile = new Profile();
            profile.setUser(user);


            // Getting the basic data object that contains the rent values from HudUser api.
            List<BasicData> basicData = apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getBasicdata();


            if (basicData.size() > 1) {
                //Getting the state code from the zip code to get EAI and utilities database data using zipCodeStack api. Only works when the
                // metro area has many zipcodes associated with it. If only one set of data, not zip code available so won't work
                // todo: NEED TO IMPLEMENT TO WHEN MANY STATES ARE ASSOCIATED WITH THE METRO AREA but no zip code available. Need to create a form so the user will select the state

                stateCode = apiServiceZipCodeStack.getZipCodeData(basicData.get(dataindex).getZipCode());//getting the state code from the zip code, zip code stack api

                model.addAttribute("rentValues", basicData.get(dataindex));
                model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
                model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
                model.addAttribute("stateCode", stateCode);
                model.addAttribute("location", "MetroArea: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());

                //Setting the profile state code from the zip code(zip code stack api)
                profile.setStateCode(stateCode);

                //Setting the profile name and location when zip code is available
                profile.setLocation("Metro Area: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());
                profile.setProfileName(userAuth.getFirstName() + "'s /Search Profile:" +" Metro Area: " +  apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());
            }
            // to manage when the data has only one basic data object
            if (basicData.size() == 1) {
                stateCode = apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode).get(0);

                model.addAttribute("rentValues", basicData.get(0));
                model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
                model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
                model.addAttribute("stateCode", stateCode);
                model.addAttribute("location", "MetroArea: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getMetroName());

                //Setting the profile state code from the hud user api(original to get a list of states associated with the metro area)
                // now taken the first state code from the list. Because here only exists one set of data
                profile.setStateCode(stateCode);

                profile.setLocation("Metro Area: " +apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName());
                profile.setProfileName(userAuth.getFirstName() + "'s /Search Profile:" +"Metro Area: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName());
            }

            //Saving all the profile things once the profile is created and set with the user
            user.getProfiles().add(profile);
            // this is not user from security context. when using the one from sc it was adding a token to the authority db(wrong)
            // don't know if using the user from db is the best approach but, it works for now. I fixed this before when updating user data, DON'T REMEMBER HOW
            userServiceImpl.save(user);

            profileService.saveProfile(profile);
            model.addAttribute("profile", profile);
            model.addAttribute("profileId", profile.getProfileId());


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

    @GetMapping("/{userId}/counties/{stateCode}/data/{dataEntityCode}/dataid/{dataindex}")
    // EN-POINT STATE/COUNTY/ZIPCODE SEARCHING CRITERIA FINAL DATA
    public String getSpecificDataByCountyCodeAndCreateProfile(@PathVariable Integer userId, @PathVariable String stateCode, @PathVariable String dataEntityCode,
                                                              @PathVariable Integer dataindex, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            model.addAttribute("user", userAuth);

            User user = userServiceImpl.findUserById(userId).get();// we don't want to use the user from the security context
            // we used the user from the security context to make sure when accessing the view and editing the fields to
            // avoid any possible manipulation of the URL. But when creating the profile we need to use the user from the db

            //to manage quantities(gallons of fuel and persons) values need it to calculate fuel and elect costs
            Integer gallonsOfFuel = 0;
            Integer persons = 0;

            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
            model.addAttribute("stateCode", stateCode);
            model.addAttribute("entityCode", dataEntityCode);
            model.addAttribute("dataindex", dataindex);

            model.addAttribute("gallonsOfFuel", gallonsOfFuel);
            model.addAttribute("personsLivingIn", persons);

            // Using to know if the data is coming from metroArea or state/county/zipCode(data.countyName empty means metroArea)
            model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));


            //Creating utilities profile, so the user will add values later to analyze and get total cost.
            Profile profile = new Profile();
            profile.setUser(user);
            profile.setStateCode(stateCode);


            // Getting the basic data object that contains the rent values from HudUser api.
            List<BasicData> basicData = apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getBasicdata();

            if (basicData.size() > 1) {
                // basicData.size() > 1 means that the county has many zipcodes associated with it, so many data sets
                model.addAttribute("rentValues", basicData.get(dataindex));
                model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
                model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
                model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());

                //Setting the profile name and location when zip code is available
                profile.setLocation(apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());
                profile.setProfileName(userAuth.getFirstName() + "'s /Search Profile:" + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());
            }
            if (basicData.size() == 1) {
                // basicData.size() == 1 means that the county has only one zipcode associated with it, so only one data set
                model.addAttribute("rentValues", basicData.get(0));
                model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
                model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
                model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getCountyName());

                //Setting the profile name and location when zip code is not available
                profile.setLocation(apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName());
                profile.setProfileName(userAuth.getFirstName() + "'s /Search Profile:" + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName());

            }

            //Saving all the profile things once the profile is created and set with the user
            user.getProfiles().add(profile);
            // this is not user from security context. when using the one from sc it was adding a token to the authority db(wrong)
            // don't know if using the user from db is the best approach but, it works for now. I fixed this before when updating user data, DON'T REMEMBER HOW
            userServiceImpl.save(user);

            profileService.saveProfile(profile);
//            model.addAttribute("profile", profile);
            model.addAttribute("profileId", profile.getProfileId());

            return "usersession";
        }
        return "redirect:/signin";
    }

    @PostMapping("/{userId}/profile/{profileId}/update")
    public String updateProfileCreated(@PathVariable Integer userId, @PathVariable Long profileId, @RequestParam(required = false) Double rentType,
                                       @RequestParam(required = false) Double fuelType, @RequestParam(required = false) Double electType,
                                        @RequestParam(required = false) Double wasteType, @RequestParam(required = false) Double waterType,
                                        @RequestParam(required = false) Double transpType, @RequestParam(required = false) Double gasType,
                                       @RequestParam(required = false) Double internetType, @RequestParam(required = false) Integer fuelQty,
                                       @RequestParam(required = false) Integer personsQty, Model model, Authentication authentication) {

        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();

            Profile profile = profileService.getProfileById(profileId);

            profile.setRentCost(rentType);
            profile.setFuelCost(fuelType * fuelQty);

            // Setting the elect cost is complicated based in all the aspects to consider, and this approach is not
            // the objective of this project, however we did try our best to provide approximated values based on household
            // members
            if( personsQty == 1) {
                profile.setElectricityCost(electType * 850 * 0.01);}
            if( personsQty == 2) {
                profile.setElectricityCost(electType * 960 * 0.01);}
            if( personsQty == 3) {
                profile.setElectricityCost(electType * 1160 * 0.01);}
            if( personsQty == 4) {
                profile.setElectricityCost(electType * 1320 * 0.01);}
            if( personsQty >= 5) {
                profile.setElectricityCost(electType * 1500 * 0.01);}

            profile.setWasteCost(wasteType);
            profile.setWaterCost(waterType);
            profile.setPublicTransportationCost(transpType);
            profile.setNaturalGasCost(gasType);
            profile.setInternetCost(internetType);
            profileService.saveProfile(profile);


        }

        System.out.println("rentType: " + rentType);

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

            // Update the security context. This is necessary because the user's email and password have changed and the
            // user we're working with is the one from the authentication object not the one from the database. If we the
            // one from the database, we will have security issues.
            Authentication newAuth = new UsernamePasswordAuthenticationToken(existingUser, existingUser.getPassword(), existingUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return "redirect:/usersession/" + userId;
        }
        return "redirect:/signin";
    }

}
