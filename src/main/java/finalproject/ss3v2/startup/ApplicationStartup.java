package finalproject.ss3v2.startup;

import finalproject.ss3v2.service.ApisService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup {
    //To get all the states and metro areas list from the HUD API when the application starts and saved in the database
    ApisService apisService;

    public ApplicationStartup(ApisService apisService) {
        this.apisService = apisService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        apisService.fetchStatesAndMetroAreasFromApi();
    }

}
