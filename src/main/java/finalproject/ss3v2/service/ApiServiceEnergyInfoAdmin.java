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
    // THIS IS THE SERVICE CLASS THAT WILL INTERACT WITH THE EIA API( TO GET THE ELECTRICITY RATES DATA)
    @Value("${eia.baseURL}")
    private String eiaBaseURL;

    @Value("${eia.apiKey}")
    private String eiaApiKey;

    private ElectResponseData responseData;


    public Double getEnergyRateByState(String stateCode) {
        // elect rates for PR(Puerto Rico), AS(American Samoa), GU(Guam), VI(Virgin Islands) are hardcoded because
        // the EIA API does not provide data for them
        if (stateCode.equalsIgnoreCase("PR")) {
            return 22.57;
        } else if (stateCode.equalsIgnoreCase("AS")) {
            return 29.00;
        } else if (stateCode.equalsIgnoreCase("GU")) {
            return 26.20;
        }else if (stateCode.equalsIgnoreCase("VI")) {
            return 42.00;
        } else{
            RestTemplate restTemplate = new RestTemplate();
            URI uri = UriComponentsBuilder.fromHttpUrl(eiaBaseURL)
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
                    responseData = mapper.readValue(response.getBody(), ElectResponseData.class);
                    Double electRate = responseData.getElectData().getElectDataInfo().get(0).getPrice();
                    System.out.println();
                    return electRate;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
