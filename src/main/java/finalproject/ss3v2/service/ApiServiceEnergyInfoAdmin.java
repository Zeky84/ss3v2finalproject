package finalproject.ss3v2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.ss3v2.dto.ElectResponseData;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ApiServiceEnergyInfoAdmin {
    @Value("${eia.baseURL}")
    private String eiaBaseURL;

    @Value("${eia.apiKey}")
    private String eiaApiKey;


    private ElectResponseData responseData;


    public ElectResponseData getEnergyRateByStateDebug(String stateCode) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = UriComponentsBuilder.fromHttpUrl(eiaBaseURL)
                .path("/v2/electricity/retail-sales/data")
                .queryParam("api_key", eiaApiKey)
                .queryParam("data[]", "price")
                .queryParam("facets[sectorid][]", "RES")
                .queryParam("facets[stateid][]", stateCode)
                .queryParam("frequency", "monthly")
                .queryParam("start", "2024-01")
                .build()
                .encode()
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                ElectResponseData responseData = mapper.readValue(response.getBody(), ElectResponseData.class);
                return responseData;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
