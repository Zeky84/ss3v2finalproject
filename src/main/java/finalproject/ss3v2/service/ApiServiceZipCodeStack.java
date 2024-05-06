package finalproject.ss3v2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.ss3v2.dto.ZipCodeResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ApiServiceZipCodeStack {

    @Value("${zipcodestack.baseURL}")
    private String zipcodestackBaseURL;

    @Value("${zipcodestack.apikey}")
    private String zipcodestackApiKey;

    private ZipCodeResults responseData;

    public String getZipCodeData(String zipCode) {
        //This service class is created to get the state code from the zip code when the user chooses to do a research
        // by metro area and the metro area has more than one state associated with it and zipcodes. So when getting the zip code,
        // now we can get the state code to get the rest of the utilities cost data from the EIA API and from the
        // previous created database. We're using here the ZipCodeStack API.
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromHttpUrl(zipcodestackBaseURL)
                .queryParam("apikey", zipcodestackApiKey)
                .queryParam("codes", zipCode)
                .queryParam("country", "US")
                .build()
                .encode()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                responseData = mapper.readValue(response.getBody(), ZipCodeResults.class);
                String stateCode = responseData.getResults().get(zipCode).get(0).getStateCode();
                return stateCode;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
