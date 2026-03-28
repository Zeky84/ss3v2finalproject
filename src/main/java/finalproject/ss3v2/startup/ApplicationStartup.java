package finalproject.ss3v2.startup;

import finalproject.ss3v2.service.ApiServiceHudUser;
import finalproject.ss3v2.service.CsvDataService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup {//This class is used to run some code when the application starts
    //To get all the states and metro areas list from the HUD API when the application starts and saved in the database
    //To create the database with the CSV data when the application starts(all the cost living values except rent and electricity rates)
    ApiServiceHudUser apiServiceHudUser;
    CsvDataService csvDataService;

    public ApplicationStartup(ApiServiceHudUser apiServiceHudUser , CsvDataService csvDataService) {

        this.apiServiceHudUser = apiServiceHudUser;
        this.csvDataService = csvDataService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        try {
            apiServiceHudUser.fetchStatesAndMetroAreasFromHudUserApi();
            System.out.println("HUD data loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load HUD data");
            e.printStackTrace();
        }

        try {
            csvDataService.createDbWithCsvData();
            System.out.println("CSV data loaded successfully");
        } catch (Exception e) {
            System.err.println("Failed to load CSV data");
            e.printStackTrace();
        }
    }

}
