package finalproject.ss3v2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.ss3v2.domain.ApiMetroAreas;
import finalproject.ss3v2.domain.ApiState;
import finalproject.ss3v2.dto.BasicData;
import finalproject.ss3v2.dto.County;
import finalproject.ss3v2.dto.DataRent;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ApiServiceHudUser {
    // THIS IS THE SERVICE CLASS THAT WILL INTERACT WITH THE HUD USER API( TO GET THE RENT DATA)
    // This class is the one that not only is uses to get the data rent from the HUD User API but also to get the
    // the States and Metro Areas to look the info based on them.
    private ApiStateRepository stateRepository;
    private ApiMetroAreaRepository metroAreaRepository;

    private static final Logger logger = LoggerFactory.getLogger(ApiServiceHudUser.class);

    @Value("${huduser.accesstoken}")
    private String hudUserAccessToken;

    @Value("${huduser.baseURL}")
    private String hudUserBaseURL;


    public ApiServiceHudUser(ApiStateRepository stateRepository, ApiMetroAreaRepository metroAreaRepository) {
        this.stateRepository = stateRepository;
        this.metroAreaRepository = metroAreaRepository;

    }

    public void fetchStatesAndMetroAreasFromHudUserApi() {
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

    public DataRent getTheDataCostByCode(String code) {
        // The data response has a field basicdata that can be either an object or an array of objectslo, that's why we need to
        // handle it differently creating a CUSTOM DESERIALIZER, instead of using the mapper.readValue method
        // directly. CHAT GPT_4 HELP!!!
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        URI uri = buildUri("/data/" + code);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode dataNode = rootNode.path("data");
            DataRent dataRent = new DataRent();

            if (dataNode.isMissingNode() || dataNode.isNull()) {
                // Handle case where no data node is found
                return null;
            }

            JsonNode basicDataNode = dataNode.path("basicdata");
            if (basicDataNode.isArray()) {
                List<BasicData> basicDataList = mapper.convertValue(basicDataNode, new TypeReference<List<BasicData>>() {});
                dataRent.setBasicdata(basicDataList);
            } else if (basicDataNode.isObject()) {
                BasicData basicData = mapper.convertValue(basicDataNode, BasicData.class);
                dataRent.setBasicdata(Collections.singletonList(basicData));
            }

            // Deserializing other fields
            dataRent.setCountyName(dataNode.path("county_name").asText());
            dataRent.setCountiesMsa(dataNode.path("counties_msa").asText());
            dataRent.setTownName(dataNode.path("town_name").asText());
            dataRent.setMetroStatus(dataNode.path("metro_status").asText());
            dataRent.setMetroName(dataNode.path("metro_name").asText());
            dataRent.setAreaName(dataNode.path("area_name").asText());
            dataRent.setSmallAreaStatus(dataNode.path("smallarea_status").asText());
            dataRent.setYear(dataNode.path("year").asText());

            return dataRent;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
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

    public List<String> getAllStatesCodesInMetroArea(String metroAreaCode) {
        String metroName = getTheDataCostByCode(metroAreaCode).getMetroName();
        List<String> statesCodes = new ArrayList<>();

        // Split the string to separate the metropolitan area from the state codes
        String[] parts = metroName.split(", ");

        if (parts.length > 1) {
            // Further split to get individual state codes
            String[] codes = parts[1].split("-");
            statesCodes.addAll(Arrays.asList(codes));
        }
        return statesCodes;
    }


}
