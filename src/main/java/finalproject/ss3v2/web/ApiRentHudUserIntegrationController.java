//package finalproject.ss3v2.web;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import finalproject.ss3v2.domain.ApiMetroAreas;
//import finalproject.ss3v2.domain.ApiState;
//import finalproject.ss3v2.repository.ApiMetroAreaRepository;
//import finalproject.ss3v2.repository.ApiStateRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.net.URI;
//import java.util.List;
//
//@Controller
//public class ApiRentHudUserIntegrationController {// ASSIGNMENT 10
//    //NOTE: The Data is grouped by States/Counties and MetroAreas.
//    // The retrieve information field for Counties and MetroAreas is different, for Counties: 'flips-code',
//    // for MetroAreas: 'cbsa-code',
//    // Logger for debugging and maintenance
//    // DBs created for States, MetroAreas and profile information
//
//    private static final Logger logger = LoggerFactory.getLogger(ApiRentHudUserIntegrationController.class);
//
//    private ApiStateRepository stateRepository;
//    private ApiMetroAreaRepository metroAreaRepository;
//
//    public ApiRentHudUserIntegrationController(ApiStateRepository stateRepository, ApiMetroAreaRepository metroAreaRepository) {
//        this.stateRepository = stateRepository;
//        this.metroAreaRepository = metroAreaRepository;
//    }
//
//    @Value("${huduser.accesstoken}")
//    private String hudUserAccessToken;
//
//    @Value("${huduser.baseURL}")
//    private String hudUserBaseURL;
//
//
//    @GetMapping("/api/HudUser/AllStatesAndMetroAreas")
//    @ResponseBody
//    public ResponseEntity<String> getStatesAndMetroAreasList() {
//        //List of States and MetroAreas are retrieved from the HUD User API and stored in a database if
//        // they are not already present. This is done this way cause the user will need to have this list to chose
//        // the find info criteria, by State or MetroArea and there is no need to call the API every time the user
//        // wants to see the list of States and MetroAreas. Saving api calls for this fields.
//        try {
//            if (stateRepository.count() == 0 && metroAreaRepository.count() == 0) {
//                RestTemplate rt = new RestTemplate();
//                HttpHeaders headers = createHeaders();
//                HttpEntity<String> entity = new HttpEntity<>(headers);
//                URI uriState = buildUri("/listStates");
//                URI uriMetroArea = buildUri("/listMetroAreas");
//
//                // Getting the response as a String
//                ResponseEntity<String> responseS = rt.exchange(uriState, HttpMethod.GET, entity, String.class);
//                ResponseEntity<String> responseM = rt.exchange(uriMetroArea, HttpMethod.GET, entity, String.class);
//
//                // Deserialize the JSON response into List<State>
//                ObjectMapper mapper = new ObjectMapper();
//                List<ApiState> states = mapper.readValue(responseS.getBody(), new TypeReference<List<ApiState>>() {
//                });
//                List<ApiMetroAreas> metroAreas = mapper.readValue(responseM.getBody(), new TypeReference<List<ApiMetroAreas>>() {
//                });
//
//                // Save the states and metro areas to the database(could create a service to do this, but for better,
//                // understanding of the process, it is done here)
//                stateRepository.saveAll(states);
//                metroAreaRepository.saveAll(metroAreas);
//            } else {
//                logger.info("States and Metro Areas are already populated in the database.");
//                return ResponseEntity.ok("States and Metro Areas are already populated in the database.");
//            }
//
//            return ResponseEntity.ok("Operation completed successfully.");
//
//        } catch (Exception e) {
//            logger.error("Error while calling HUD User API", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
//        }
//    }
//
//    @GetMapping("/api/HudUser/listCounties/{stateCode}")
//    @ResponseBody
//    public ResponseEntity<String> getCountiesByStateList(@PathVariable String stateCode) {
//        try {
//            RestTemplate rt = new RestTemplate();
//            HttpHeaders headers = createHeaders();
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            URI uri = buildUri("/listCounties/" + stateCode);
//
//            ResponseEntity<String> response = rt.exchange(uri, HttpMethod.GET, entity, String.class);
//            return response;
//        } catch (Exception e) {
//            logger.error("Error while calling HUD User API", e);
//            // Handle the error appropriately
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
//        }
//
//    }
//
//
//
//    @GetMapping("/api/HudUser/data/{entityid}")
//    @ResponseBody
//    public ResponseEntity<String> getDataByEntityId(@PathVariable String entityid) {
//        // The entity id is the 'flips-code' for Counties and 'cbsa-code' for MetroAreas
//        try {
//            RestTemplate rt = new RestTemplate();
//            HttpHeaders headers = createHeaders();
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            URI uri = buildUri("/data/" + entityid);
//
//            ResponseEntity<String> response = rt.exchange(uri, HttpMethod.GET, entity, String.class);
//            return response;
//        } catch (Exception e) {
//            logger.error("Error while calling HUD User API", e);
//            // Handle the error appropriately
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
//        }
//    }
//
//
//    private HttpHeaders createHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + hudUserAccessToken);
//        return headers;
//    }
//
//    private URI buildUri(String enpointCompletion) {
//        return UriComponentsBuilder.fromHttpUrl(hudUserBaseURL + enpointCompletion)
//                .build()
//                .toUri();
//    }
//}
