package finalproject.ss3v2.web;
import finalproject.ss3v2.domain.Authority;
import finalproject.ss3v2.domain.Profile;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.dto.BasicData;
import finalproject.ss3v2.dto.DataRent;
import finalproject.ss3v2.repository.UtilitiesRepository;
import finalproject.ss3v2.service.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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

    @GetMapping("/{userId}") // END-POINT FOR START SEARCHING CRITERIA, SEARCH BY STATE OR METRO AREA
    public String goToUserSession(@PathVariable Integer userId, Model model, ModelMap modelMap, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            // the user from the auth object instead from the to avoid any possible manipulation of the URL
            User userAuth = (User) authentication.getPrincipal();
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data) return to its own session
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Adding the states and metro areas to the html view
            modelMap.put("states", apiServiceHudUser.getStatesList());
            modelMap.put("metroAreas", apiServiceHudUser.getMetroAreasList());

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }
            return "usersession";
        }
        return "redirect:/signin";
    }


    @PostMapping("/{userId}/asKForSuperUser")
    public String askForSuperUser(@PathVariable Integer userId, Model model, Authentication authentication) {
        // This code was helped by Chat GPT4... I was stuck in this part for a while
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            userAuth.setUserAskedForSuperUser(true);

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }

            // Clean up the user object before saving(To avoid getting undesired tokens in the dashboard table)
            cleanUpUserBeforeSave(userAuth);

            //--------------------------------------------IMPORTANT TO REMEMBER----------------------------------------
            userServiceImpl.save(userAuth); // updating the user with the new values

            //This object takes the updated user, their credentials (usually the password), and their authorities (roles/privileges).This step is crucial
            // as it reflects the changes in the user’s authentication token, ensuring that any subsequent security checks use the latest user details.
            Authentication updatedAuth = new UsernamePasswordAuthenticationToken(userAuth, authentication.getCredentials(), userAuth.getAuthorities());

            //This final step is setting the new authentication in the SecurityContext. This is important because it
            // ensures that within the current session or security context, the user's authentication details are up-to-date.
            // This affects everything from security checks to decisions made based on the user's roles or identity.
            SecurityContextHolder.getContext().setAuthentication(updatedAuth);
            //---------------------------------------------------------------------------------------------------------

            model.addAttribute("user", userAuth);


            return "redirect:/usersession/" + userId; // Ensure this is a redirect
        }
        return "redirect:/signin";
    }

    private void cleanUpUserBeforeSave(User user) {
        // Remove or reset any fields that should not be persisted
        user.getAuthorities().removeIf(auth -> !isValidAuthority(auth));
    }

    private boolean isValidAuthority(Authority authority) {
        // Ensure it doesn't contain token data
        return !authority.getAuthority().contains("eyJhbGciOiJIUzI1NiJ9");
    }


    @GetMapping("/{userId}/metroarea/data/{dataEntityCode}") // ENDPOINT METRO AREA SEARCHING CRITERIA
    public String getDataByMetroAreaCode(@PathVariable Integer userId, @PathVariable String dataEntityCode, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Adding list of profiles created by the user if they exist(this was added later)
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }

            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("entityCode", dataEntityCode);

            if (apiServiceHudUser.getTheDataCostByCode(dataEntityCode) != null) {
                // The previous code was changed. When trying to input search in the list of zip codes to localize it easier
                // the input search was not working. Because instead of sending a list of zip codes to the front end, i was
                // getting and object (Data) with a nested List<BasicData> object. So I changed the code to send a list of zip codes
                // now works.07/15/2024
                DataRent data = apiServiceHudUser.getTheDataCostByCode(dataEntityCode);
                if (Objects.equals(data.getSmallAreaStatus(), "1")) {
                    model.addAttribute("smallAreaStatus", "1"); // small area status of 1 means that the metro area has many zip codes associated with it
                    List<String> AllZipCodes = new ArrayList<>();
                    List<BasicData> basicdata = data.getBasicdata();
                    for (BasicData dataset : basicdata) {
                        if (!Objects.equals(dataset.getZipCode(), "88888")) {// filtering the zip code 88888 which is a placeholder and no state associated with it
                            AllZipCodes.add(dataset.getZipCode());
                        }
                    }
                    model.addAttribute("zipCodes", AllZipCodes);
                } else {
                    model.addAttribute("smallAreaStatus", "0");
                    model.addAttribute("metroName", data.getMetroName());
                }
                model.addAttribute("data", data);
            } else {
                //found a case where no data was found... for example Arecibo, PR
                model.addAttribute("nodata", "no data found for this search");
            }

            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/metroarea/data/{dataEntityCode}/dataid/{dataindex}")
    // ENDPOINT METroAREA SEARCH CRITERIA FINAL DATA
    public String getSpecificDataByMetroAreaCode(@PathVariable Integer userId, @PathVariable String dataEntityCode, Model model,
                                                 @PathVariable Integer dataindex, Authentication authentication) {

        String stateCode; // to store the state code when using zip code stack api. When metro-area have has more than one
        // zip code associated with it, it's hard to get the state code. but having the zip code allows to get the state code
        // using the API zip code stack.

        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }


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
                profile.setProfileName(" Metro Area: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
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

                profile.setLocation("Metro Area: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName());
                profile.setProfileName("Metro Area: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getMetroName());
            }

            //Saving all the profile things once the profile is created and set with the user
            user.getProfiles().add(profile);
            userServiceImpl.save(user);

            profileService.saveProfile(profile);
            model.addAttribute("profileId", profile.getProfileId());


            return "usersession";
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/counties/{stateCode}") // EN-POINT FOR STATE-COUNTY SEARCHING CRITERIA
    public String getCountiesByState(@PathVariable Integer userId, @PathVariable String stateCode, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }

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
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }

            if (apiServiceHudUser.getTheDataCostByCode(dataEntityCode) != null) {
                // The previous code was changed. When trying to input search in the list of zip codes to localize it easier
                // the input search was not working. Instead of sending a list of zip codes to the front end, it was
                // sending and object (Data) with a nested List<BasicData> object. Now works.

                DataRent data = apiServiceHudUser.getTheDataCostByCode(dataEntityCode);
                if (Objects.equals(data.getSmallAreaStatus(), "1") && !Objects.equals(data.getCountyName(), "")) {
                    model.addAttribute("smallAreaStatus", "1"); // small area status of 1 means that the county area has many zip codes associated with it
                    List<String> AllZipCodes = new ArrayList<>();
                    List<BasicData> basicdata = data.getBasicdata();
                    for (BasicData groupset : basicdata) {
                        AllZipCodes.add(groupset.getZipCode());

                    }
                    model.addAttribute("zipCodes", AllZipCodes);
                } else {
                    model.addAttribute("smallAreaStatus", "0");
                    model.addAttribute("countyName", data.getCountyName());
                }
                model.addAttribute("data", data);
            } else {
                //found a case where no data was found... for example Arecibo, PR
                model.addAttribute("nodata", "no data found for this search");
            }

            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
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
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }


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
                profile.setProfileName(apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
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
                profile.setProfileName(apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                        .getCountyName());

            }

            //Saving all the profile things once the profile is created and set with the user
            user.getProfiles().add(profile);
            userServiceImpl.save(user);

            profileService.saveProfile(profile);
            model.addAttribute("profileId", profile.getProfileId());

            return "usersession";
        }
        return "redirect:/signin";
    }

    @PostMapping("/{userId}/profile/{profileId}/update")
    public String addValuesToProfileCreated(@PathVariable Integer userId, @PathVariable Long profileId, @RequestParam(required = false) Double rentType,
                                            @RequestParam(required = false) Double fuelType, @RequestParam(required = false) Double electType,
                                            @RequestParam(required = false) Double wasteType, @RequestParam(required = false) Double waterType,
                                            @RequestParam(required = false) Double transpType, @RequestParam(required = false) Double gasType,
                                            @RequestParam(required = false) Double internetType, @RequestParam(required = false) Integer fuelQty,
                                            @RequestParam(required = false) Integer personsQty, Model model, Authentication authentication) {


        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }

            //Checking no null values before updating the profile(null values are set to 0)
            if (rentType == null) {
                rentType = 0.0;
            }
            if (fuelType == null) {
                fuelType = 0.0;
            }
            if (electType == null) {
                electType = 0.0;
            }
            if (wasteType == null) {
                wasteType = 0.0;
            }
            if (waterType == null) {
                waterType = 0.0;
            }
            if (transpType == null) {
                transpType = 0.0;
            }
            if (gasType == null) {
                gasType = 0.0;
            }
            if (internetType == null) {
                internetType = 0.0;
            }

            Profile profile = profileService.getProfileById(profileId);

            profile.setRentCost(rentType);
            // Setting the values up to two decimal places
            profile.setFuelCost(Math.round(fuelType * fuelQty * 100.0) / 100.0);

            // Setting the elect cost is complicated based in all the aspects to consider and this approach is not
            // the objective of this project, however did try our best to provide approximated values based on household
            // members
            if (personsQty == 0) {
                profile.setElectricityCost(0.0);
            }
            if (personsQty == 1) {
                profile.setElectricityCost((double) Math.round(electType * 850 * 0.01));
            }
            if (personsQty == 2) {
                profile.setElectricityCost((double) Math.round(electType * 960 * 0.01));
            }
            if (personsQty == 3) {
                profile.setElectricityCost((double) Math.round(electType * 1160 * 0.01));
            }
            if (personsQty == 4) {
                profile.setElectricityCost((double) Math.round(electType * 1320 * 0.01));
            }
            if (personsQty >= 5) {
                profile.setElectricityCost((double) Math.round(electType * 1500 * 0.01));
            }

            profile.setWasteCost(wasteType);
            profile.setWaterCost(waterType);
            profile.setPublicTransportationCost(transpType);
            profile.setNaturalGasCost(gasType);
            profile.setInternetCost(internetType);
            profile.setTotalCost((double) Math.round(rentType + (fuelType * fuelQty) + profile.getElectricityCost() + wasteType + waterType + transpType + gasType + internetType));
            profileService.saveProfile(profile);

            return "redirect:/usersession/" + userId;
        }

        return "redirect:/signin";
    }

    @GetMapping("/{userId}/profile/{profileId}/delete")
    public String deleteProfile(@PathVariable Integer userId, @PathVariable Long profileId, Model
            model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";

            }

            User user = userServiceImpl.findUserByEmail(userAuth.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));;// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            model.addAttribute("user", userAuth);
            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }

            //Removing the profile from the user's profile list and deleting the profile(6/15/2024)
            //Because the way JPA works,we need to remove the profile from both sides of the relationship to avoid
            // data integrity issues,such as having a Profile in the collection that no longer exists in the database.
            user.getProfiles().remove(profileService.getProfileById(profileId));
            profileService.deleteProfileById(profileId);


            return "redirect:/usersession/" + userId;
        }
        return "redirect:/signin";
    }

    @GetMapping("/{userId}/profile/{profileId}/piechart")
    public String generatePieChart(@PathVariable Integer userId, @PathVariable Long profileId, Model model, Authentication authentication) {
		User userAuth = null; //Bringing the user to the scope in the last line of the method
		if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
			userAuth = (User) authentication.getPrincipal();
			User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

			if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
				return "redirect:/usersession/" + userAuth.getId() + "?unAuthorized";
			}
			model.addAttribute("user", userAuth);

			// Adding the states and metro areas to the html view
			model.addAttribute("states", apiServiceHudUser.getStatesList());
			model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());

			// Adding list of profiles created by the user if they exists
			List<Profile> profiles = user.getProfiles();
			if (!profiles.isEmpty()) {
				model.addAttribute("profiles", profiles);
			}

			// Getting the profile to get the data to create the pie chart. Because is used the profileId to get the profile
			//  is needed to check if the profile belongs to the current user and not to another user. Current user can access
			// other user's data if the profileId is manipulated in the URL. Checking if profile id is in the user's profile list
			// No url manipulation anymore 08/04/2024
			for (Profile profile : profiles) {
				if (profile.getProfileId().equals(profileId)) {
					profile = profileService.getProfileById(profileId);
					Map<String, Double> pieData = new HashMap<>();

					if (profile.getTotalCost() != null && profile.getTotalCost() > 0) {
						double totalCost = profile.getTotalCost();
						Map<String, Double> costMap = Map.of(
								"RentCost", profile.getRentCost() != null ? profile.getRentCost() : 0.0,
								"FuelCost", profile.getFuelCost() != null ? profile.getFuelCost() : 0.0,
								"ElectCost", profile.getElectricityCost() != null ? profile.getElectricityCost() : 0.0,
								"WasteCost", profile.getWasteCost() != null ? profile.getWasteCost() : 0.0,
								"WaterCost", profile.getWaterCost() != null ? profile.getWaterCost() : 0.0,
								"TransCost", profile.getPublicTransportationCost() != null ? profile.getPublicTransportationCost() : 0.0,
								"NatGasCost", profile.getNaturalGasCost() != null ? profile.getNaturalGasCost() : 0.0,
								"InternetCost", profile.getInternetCost() != null ? profile.getInternetCost() : 0.0
						);
						for (Map.Entry<String, Double> entry : costMap.entrySet()) {
							Double cost = entry.getValue();
							if (cost > 0) {
								pieData.put(entry.getKey() + ": $" + cost, (cost / totalCost) * 100);
							}
						}

						model.addAttribute("pieData", pieData);
						return "usersession";
					}

					System.out.println("No data to create the pie chart");
					model.addAttribute("error", "No data to create the pie chart");

					return "usersession";
				}
				System.out.println("No data to create the pie chart");
				model.addAttribute("error", "No data to create the pie chart");
			}
		}
		return "redirect:/usersession/" + userAuth.getId() + "?unAuthorized";
	}


    @PostMapping("/{userId}/generateBarChart")
    public String processSelectedProfiles(@PathVariable Integer userId, @RequestParam("selectedProfiles") List<Long> selectedProfileIds, Model model,
                                          Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();
            User user = userServiceImpl.findUserByEmail(userAuth.getEmail()).get();// accessing the user from the db using the email from the userAuth, to avoid any possible manipulation of the URL 08/04/2024

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data)08/04/2024
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
            model.addAttribute("user", userAuth);

            // Fetch profiles based on selectedProfileIds
            List<Profile> selectedProfiles = profileService.getProfilesByIds(selectedProfileIds);

            // Adding the states and metro areas to the html view
            model.addAttribute("states", apiServiceHudUser.getStatesList());
            model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = user.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }


            Map<String, Map<String, Double>> barChartData = new HashMap<>();
            for (Profile profile : selectedProfiles) {
                Map<String, Double> profileData = new HashMap<>();
                profileData.put("rentCost", profile.getRentCost() != null ? profile.getRentCost() : 0.0);
                profileData.put("fuelCost", profile.getFuelCost() != null ? profile.getFuelCost() : 0.0);
                profileData.put("electricityCost", profile.getElectricityCost() != null ? profile.getElectricityCost() : 0.0);
                profileData.put("wasteCost", profile.getWasteCost() != null ? profile.getWasteCost() : 0.0);
                profileData.put("waterCost", profile.getWaterCost() != null ? profile.getWaterCost() : 0.0);
                profileData.put("publicTransportationCost", profile.getPublicTransportationCost() != null ? profile.getPublicTransportationCost() : 0.0);
                profileData.put("naturalGasCost", profile.getNaturalGasCost() != null ? profile.getNaturalGasCost() : 0.0);
                profileData.put("internetCost", profile.getInternetCost() != null ? profile.getInternetCost() : 0.0);
                profileData.put("totalCost", profile.getTotalCost() != null ? profile.getTotalCost() : 0.0);

                barChartData.put(profile.getProfileName(), profileData);
            }

            model.addAttribute("barChartData", barChartData);

            return "usersession";
        }
        return "redirect:/signin";
    }


    @GetMapping("/{userId}/edituser")
    public String goToEditUser(@PathVariable Integer userId, Model model, Authentication authentication) {
        if (authentication != null && refreshTokenService.verifyRefreshTokenExpirationByUserId(((User) authentication.getPrincipal()).getId())) {
            User userAuth = (User) authentication.getPrincipal();

            if (!userAuth.getId().equals(userId)) {// to avoid any possible manipulation of the URL(current user trying to access another user's data
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }
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
            User userAuth = (User) authentication.getPrincipal();

            if (!userAuth.getId().equals(userId)) {
                // Redirect to an error page or a 'forbidden' page
                return "redirect:/usersession/" + userAuth.getId()+"?unAuthorized";
            }

            // Adding list of profiles created by the user if they exists
            List<Profile> profiles = userAuth.getProfiles();
            if (!profiles.isEmpty()) {
                model.addAttribute("profiles", profiles);
            }

            // Check if the authenticated user's ID matches the user ID from the URL to avoid unauthorized updates. just in case
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
            Authentication newAuth = new UsernamePasswordAuthenticationToken(existingUser, existingUser.getPassword(), existingUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            return "redirect:/usersession/" + userId;
        }
        return "redirect:/signin";
    }

}
