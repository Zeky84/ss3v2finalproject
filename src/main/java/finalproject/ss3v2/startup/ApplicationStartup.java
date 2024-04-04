package finalproject.ss3v2.startup;

import finalproject.ss3v2.web.ApiRentHudUserIntegrationController;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup {
    //To get all the states and metro areas list from the HUD API when the application starts
    ApiRentHudUserIntegrationController apiRentHudUserIntegrationController;

    public ApplicationStartup(ApiRentHudUserIntegrationController apiRentHudUserIntegrationController) {
        this.apiRentHudUserIntegrationController = apiRentHudUserIntegrationController;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        apiRentHudUserIntegrationController.getStatesAndMetroAreasList();
    }

}
