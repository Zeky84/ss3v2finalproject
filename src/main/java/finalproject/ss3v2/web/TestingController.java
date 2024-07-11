package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.Profile;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.dto.BasicData;
import finalproject.ss3v2.dto.TestingProfile;
import finalproject.ss3v2.repository.UtilitiesRepository;
import finalproject.ss3v2.service.ApiServiceEnergyInfoAdmin;
import finalproject.ss3v2.service.ApiServiceHudUser;
import finalproject.ss3v2.service.ApiServiceZipCodeStack;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/app-testing")
public class TestingController {

    private ApiServiceHudUser apiServiceHudUser;
    private ApiServiceEnergyInfoAdmin apiServiceEnergyInfoAdmin;

    private ApiServiceZipCodeStack apiServiceZipCodeStack;

    private UtilitiesRepository utilitiesRepository;

    public TestingController(ApiServiceHudUser apiServiceHudUser, ApiServiceEnergyInfoAdmin apiServiceEnergyInfoAdmin,
                             ApiServiceZipCodeStack apiServiceZipCodeStack, UtilitiesRepository utilitiesRepository) {
        this.apiServiceHudUser = apiServiceHudUser;
        this.apiServiceEnergyInfoAdmin = apiServiceEnergyInfoAdmin;
        this.apiServiceZipCodeStack = apiServiceZipCodeStack;
        this.utilitiesRepository = utilitiesRepository;

    }

    private TestingProfile myTestingProfile = new TestingProfile();//to store the profile created by the user to be used in the pie chart and bar chart endpoints.

    @GetMapping("")
    public String goToUserSession(Model model, ModelMap modelMap) {

        // Adding the states and metro areas to the html view
        modelMap.put("states", apiServiceHudUser.getStatesList());
        modelMap.put("metroAreas", apiServiceHudUser.getMetroAreasList());

        return "app-testing";
    }

    @GetMapping("/metroarea/data/{dataEntityCode}") // ENDPOINT METRO AREA SEARCHING CRITERIA
    public String getDataByMetroAreaCode(@PathVariable String dataEntityCode, Model model) {


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
        return "app-testing";

    }

    @GetMapping("/metroarea/data/{dataEntityCode}/dataid/{dataindex}")
    public String getSpecificDataByMetroAreaCode(@PathVariable String dataEntityCode, Model model,
                                                 @PathVariable Integer dataindex) {

        String stateCode;

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

        }
        // to manage when the data has only one basic data object
        if (basicData.size() == 1) {
            stateCode = apiServiceHudUser.getAllStatesCodesInMetroArea(dataEntityCode).get(0);

            model.addAttribute("rentValues", basicData.get(0));
            model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
            model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
            model.addAttribute("stateCode", stateCode);
            model.addAttribute("location", "MetroArea: " + apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getMetroName());

        }

        return "app-testing";
    }

    @GetMapping("/counties/{stateCode}") // EN-POINT FOR STATE-COUNTY SEARCHING CRITERIA
    public String getCountiesByState(@PathVariable String stateCode, Model model) {

        model.addAttribute("states", apiServiceHudUser.getStatesList());
        model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
        model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
        model.addAttribute("stateCode", stateCode);

        return "app-testing";
    }

    @GetMapping("/counties/{stateCode}/data/{dataEntityCode}") // EN-POINT FOR STATE-COUNTY SEARCHING CRITERIA
    public String getDataByZipCode(@PathVariable String stateCode,
                                   @PathVariable String dataEntityCode, Model model) {

        model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());
        model.addAttribute("states", apiServiceHudUser.getStatesList());
        model.addAttribute("counties", apiServiceHudUser.getCountiesListByStateCode(stateCode));
        model.addAttribute("data", apiServiceHudUser.getTheDataCostByCode(dataEntityCode));
        model.addAttribute("stateCode", stateCode);
        model.addAttribute("entityCode", dataEntityCode);

        return "app-testing";
    }

    @GetMapping("/counties/{stateCode}/data/{dataEntityCode}/dataid/{dataindex}")
    // EN-POINT STATE/COUNTY/ZIPCODE SEARCHING CRITERIA FINAL DATA
    public String getSpecificDataByCountyCodeAndCreateProfile(@PathVariable String stateCode, @PathVariable String dataEntityCode,
                                                              @PathVariable Integer dataindex, Model model) {


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


        // Getting the basic data object that contains the rent values from HudUser api.
        List<BasicData> basicData = apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getBasicdata();

        if (basicData.size() > 1) {
            // basicData.size() > 1 means that the county has many zipcodes associated with it, so many data sets
            model.addAttribute("rentValues", basicData.get(dataindex));
            model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
            model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
            model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode)
                    .getCountyName() + " / Zip Code: " + basicData.get(dataindex).getZipCode());


        }
        if (basicData.size() == 1) {
            // basicData.size() == 1 means that the county has only one zipcode associated with it, so only one data set
            model.addAttribute("rentValues", basicData.get(0));
            model.addAttribute("electRate", apiServiceEnergyInfoAdmin.getEnergyRateByStateDebug(stateCode));
            model.addAttribute("othersUtilities", utilitiesRepository.findByStateCode(stateCode));
            model.addAttribute("location", apiServiceHudUser.getTheDataCostByCode(dataEntityCode).getCountyName());
        }

        return "app-testing";
    }

    @PostMapping("/profile/create")
    public String addValuesToProfileCreated(@RequestParam(required = false) Double rentType,
                                            @RequestParam(required = false) Double fuelType, @RequestParam(required = false) Double electType,
                                            @RequestParam(required = false) Double wasteType, @RequestParam(required = false) Double waterType,
                                            @RequestParam(required = false) Double transpType, @RequestParam(required = false) Double gasType,
                                            @RequestParam(required = false) Double internetType, @RequestParam(required = false) Integer fuelQty,
                                            @RequestParam(required = false) Integer personsQty, @RequestParam String location,
                                            @RequestParam String stateCode, RedirectAttributes redirectAttributes) {

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


        myTestingProfile.setLocation(location);
        myTestingProfile.setStateCode(stateCode);
        myTestingProfile.setRentCost(rentType);
        // Setting the values up to two decimal places
        myTestingProfile.setFuelCost(Math.round(fuelType * fuelQty * 100.0) / 100.0);

        // Setting the elect cost is complicated based in all the aspects to consider and this approach is not
        // the objective of this project, however we did try our best to provide approximated values based on household
        // members
        if (personsQty == 0) {
            myTestingProfile.setElectricityCost(0.0);
        }
        if (personsQty == 1) {
            myTestingProfile.setElectricityCost((double) Math.round(electType * 850 * 0.01));
        }
        if (personsQty == 2) {
            myTestingProfile.setElectricityCost((double) Math.round(electType * 960 * 0.01));
        }
        if (personsQty == 3) {
            myTestingProfile.setElectricityCost((double) Math.round(electType * 1160 * 0.01));
        }
        if (personsQty == 4) {
            myTestingProfile.setElectricityCost((double) Math.round(electType * 1320 * 0.01));
        }
        if (personsQty >= 5) {
            myTestingProfile.setElectricityCost((double) Math.round(electType * 1500 * 0.01));
        }

        myTestingProfile.setWasteCost(wasteType);
        myTestingProfile.setWaterCost(waterType);
        myTestingProfile.setPublicTransportationCost(transpType);
        myTestingProfile.setNaturalGasCost(gasType);
        myTestingProfile.setInternetCost(internetType);
        myTestingProfile.setTotalCost((double) Math.round(rentType + (fuelType * fuelQty) + myTestingProfile.getElectricityCost() + wasteType + waterType + transpType + gasType + internetType));

        redirectAttributes.addFlashAttribute("profile", myTestingProfile);
        return "redirect:/app-testing";
    }

    @GetMapping("/profile/create/piechart")
    public String generatePieChart(Model model) {

        // Adding the states and metro areas to the html view
        model.addAttribute("states", apiServiceHudUser.getStatesList());
        model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());


        // Getting the myTestingProfile from local variable to get the data to create the pie chart
        Map<String, Double> pieData = new HashMap<>();

        if (myTestingProfile.getTotalCost() != null && myTestingProfile.getTotalCost() > 0) {
            double totalCost = myTestingProfile.getTotalCost();
            Map<String, Double> costMap = Map.of(
                    "RentCost", myTestingProfile.getRentCost() != null ? myTestingProfile.getRentCost() : 0.0,
                    "FuelCost", myTestingProfile.getFuelCost() != null ? myTestingProfile.getFuelCost() : 0.0,
                    "ElectCost", myTestingProfile.getElectricityCost() != null ? myTestingProfile.getElectricityCost() : 0.0,
                    "WasteCost", myTestingProfile.getWasteCost() != null ? myTestingProfile.getWasteCost() : 0.0,
                    "WaterCost", myTestingProfile.getWaterCost() != null ? myTestingProfile.getWaterCost() : 0.0,
                    "TransCost", myTestingProfile.getPublicTransportationCost() != null ? myTestingProfile.getPublicTransportationCost() : 0.0,
                    "NatGasCost", myTestingProfile.getNaturalGasCost() != null ? myTestingProfile.getNaturalGasCost() : 0.0,
                    "InternetCost", myTestingProfile.getInternetCost() != null ? myTestingProfile.getInternetCost() : 0.0
            );
            for (Map.Entry<String, Double> entry : costMap.entrySet()) {
                Double cost = entry.getValue();
                if (cost > 0) {
                    pieData.put(entry.getKey() + ": $" + cost, (cost / totalCost) * 100);
                }
            }
            model.addAttribute("profile", myTestingProfile);
            model.addAttribute("pieData", pieData);
            return "app-testing";
        }

        System.out.println("No data to create the pie chart");
        model.addAttribute("error", "No data to create the pie chart");

        return "app-testing";
    }

    @PostMapping("/profile/create/generateBarChart")
    public String processSelectedProfiles(Model model, RedirectAttributes redirectAttributes) {
        // Adding the states and metro areas to the html view
        model.addAttribute("states", apiServiceHudUser.getStatesList());
        model.addAttribute("metroAreas", apiServiceHudUser.getMetroAreasList());

        List<TestingProfile> profiles =  new ArrayList<>(); // It doesn't have sense to create a list here to add only one profile,
        // but it is done like that because I'm lazy and don't want to change the code in the html view to accept a single object
        profiles.add(myTestingProfile);

        Map<String, Map<String, Double>> barChartData = new HashMap<>();
        for (TestingProfile profile : profiles) {
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

            barChartData.put("testing session/Profile" + myTestingProfile.getLocation(), profileData);
        }

        redirectAttributes.addFlashAttribute("profile", myTestingProfile);
        redirectAttributes.addFlashAttribute("barChartData", barChartData);

        return "redirect:/app-testing";

    }


}
