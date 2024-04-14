package finalproject.ss3v2.startup;

import finalproject.ss3v2.service.ApiServiceHudUser;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup {
    //To get all the states and metro areas list from the HUD API when the application starts and saved in the database
    //To get the fuel cost by state(regular, midgrade, premium, diesel) could be weekly, monthly is too long to wait
    ApiServiceHudUser apiServiceHudUser;

    public ApplicationStartup(ApiServiceHudUser apiServiceHudUser) {
        this.apiServiceHudUser = apiServiceHudUser;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        apiServiceHudUser.fetchStatesAndMetroAreasFromHudUserApi();
    }

}
