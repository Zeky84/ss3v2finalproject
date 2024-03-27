package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
public class ApiRentHudUserIntegrationController {// ASSIGNMENT 10
    //NOTE: The Data is grouped by States/Counties and MetroAreas.
    // The retrieve information field for Counties and MetroAreas is different, for Counties: 'flips-code',
    // for MetroAreas: 'cbsa-code',
    // Logger for debugging and maintenance

    private static final Logger logger = LoggerFactory.getLogger(ApiRentHudUserIntegrationController.class);

    @Value("${huduser.accesstoken}")
    private String hudUserAccessToken;

    @Value("${huduser.baseURL}")
    private String hudUserBaseURL;


    @GetMapping("/api/HudUser/AllStates")
    public ResponseEntity<String> getStatesList() {
        // Trevor's code didn't work here cause the way this API works
        // , so I had to change it to this existing code, CHAT GPT-4 suggestions
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = createHeaders();

            HttpEntity<String> entity = new HttpEntity<>(headers);
            URI uri = buildUri("/listStates");

            ResponseEntity<String> response = rt.exchange(uri, HttpMethod.GET, entity, String.class);
            return response;
        } catch (Exception e) {
            logger.error("Error while calling HUD User API", e);
            // Handle the error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
    @GetMapping("/api/HudUser/allMetroAreas")
    public ResponseEntity<String> getMetroAreasList() {
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = createHeaders();

            HttpEntity<String> entity = new HttpEntity<>(headers);
            URI uri = buildUri("/listMetroAreas");

            ResponseEntity<String> response = rt.exchange(uri, HttpMethod.GET, entity, String.class);
            return response;
        } catch (Exception e) {
            logger.error("Error while calling HUD User API", e);
            // Handle the error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
    @GetMapping("/api/HudUser/listCounties/{stateCode}")
    public ResponseEntity<String> getCountiesByStateList(@PathVariable String stateCode) {
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = createHeaders();

            HttpEntity<String> entity = new HttpEntity<>(headers);
            URI uri = buildUri("/listCounties/" + stateCode);

            ResponseEntity<String> response = rt.exchange(uri, HttpMethod.GET, entity, String.class);
            return response;
        } catch (Exception e) {
            logger.error("Error while calling HUD User API", e);
            // Handle the error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @GetMapping("/api/HudUser/data/{entityid}")
    public ResponseEntity<String> getDataByEntityId(@PathVariable String entityid) {
        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = createHeaders();

            HttpEntity<String> entity = new HttpEntity<>(headers);
            URI uri = buildUri("/data/" + entityid);

            ResponseEntity<String> response = rt.exchange(uri, HttpMethod.GET, entity, String.class);
            return response;
        } catch (Exception e) {
            logger.error("Error while calling HUD User API", e);
            // Handle the error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hudUserAccessToken);
        return headers;
    }

    private URI buildUri(String enpointCompletion) {
        return UriComponentsBuilder.fromHttpUrl(hudUserBaseURL + enpointCompletion)
                .build()
                .toUri();
    }
}
