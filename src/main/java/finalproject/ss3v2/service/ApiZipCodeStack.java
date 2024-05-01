package finalproject.ss3v2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiZipCodeStack {

    @Value("${zipcodestack.baseURL}")
    private String eiaBaseURL;

    @Value("${zipcodestack.apikey}")
    private String eiaApiKey;
}
