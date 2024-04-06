package finalproject.ss3v2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.ss3v2.domain.ApiMetroAreas;
import finalproject.ss3v2.domain.ApiState;
import finalproject.ss3v2.dto.BasicData;
import finalproject.ss3v2.dto.County;
import finalproject.ss3v2.repository.ApiMetroAreaRepository;
import finalproject.ss3v2.repository.ApiStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class ApisService {
    private ApiStateRepository stateRepository;
    private ApiMetroAreaRepository metroAreaRepository;

    private static final Logger logger = LoggerFactory.getLogger(ApisService.class);

    @Value("${huduser.accesstoken}")
    private String hudUserAccessToken;

    @Value("${huduser.baseURL}")
    private String hudUserBaseURL;


    public ApisService(ApiStateRepository stateRepository, ApiMetroAreaRepository metroAreaRepository) {
        this.stateRepository = stateRepository;
        this.metroAreaRepository = metroAreaRepository;
        ;
    }

    public void fetchStatesAndMetroAreasFromApi() {
        // Fetch the states and metro areas from the HUD User API when the application starts and
        // save them into the DBs
        try {
            if (stateRepository.count() == 0 && metroAreaRepository.count() == 0) {
                RestTemplate rt = new RestTemplate();
                HttpHeaders headers = createHeaders();
                HttpEntity<String> entity = new HttpEntity<>(headers);
                URI uriState = buildUri("/listStates");
                URI uriMetroArea = buildUri("/listMetroAreas");

                // Getting the response as a String
                ResponseEntity<String> responseS = rt.exchange(uriState, HttpMethod.GET, entity, String.class);
                ResponseEntity<String> responseM = rt.exchange(uriMetroArea, HttpMethod.GET, entity, String.class);

                // Deserialize the JSON response into List<State>
                ObjectMapper mapper = new ObjectMapper();
                List<ApiState> states = mapper.readValue(responseS.getBody(), new TypeReference<List<ApiState>>() {
                });
                List<ApiMetroAreas> metroAreas = mapper.readValue(responseM.getBody(), new TypeReference<List<ApiMetroAreas>>() {
                });

                // Save the states and metro areas to the database(could create a service to do this, but for better,
                // understanding of the process, it is done here)
                stateRepository.saveAll(states);
                metroAreaRepository.saveAll(metroAreas);
            } else {
                logger.info("States and Metro Areas are already populated in the database.");
                System.out.println("States and Metro Areas are already populated in the database.");
            }

            System.out.println("Operation completed successfully.");

        } catch (Exception e) {
            logger.error("Error while calling HUD User API", e);
            System.out.println("Internal Server Error");
        }
    }

    public List<County> getCountiesListByStateCode(String stateCode) {


        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        URI uri = buildUri("/listCounties/" + stateCode);

        // Getting the response as a String
        ResponseEntity<String> responseC = rt.exchange(uri, HttpMethod.GET, entity, String.class);

        // Deserialize the JSON response into List<County>
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseC.getBody(), new TypeReference<List<County>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BasicData> getTheDataCostByCode(String code){
        // Fetching the data cost by code. If choosing through the county the code is the fips_code,
        // if metro area the code is cbsa_code


        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        URI uri = buildUri("/data/" + code);

        // Getting the response as a String
        ResponseEntity<String> responseC = rt.exchange(uri, HttpMethod.GET, entity, String.class);

        // Deserialize the JSON response into List<County>
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseC.getBody(), new TypeReference<List<BasicData>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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

    public List<ApiState> getStatesList() {
        return stateRepository.findAll();
    }

    public List<ApiMetroAreas> getMetroAreasList() {
        return metroAreaRepository.findAll();
    }


}
