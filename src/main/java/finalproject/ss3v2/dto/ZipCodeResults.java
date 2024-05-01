package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZipCodeResults {
    private Map<String, List<ZipCodeData>> results;

    public Map<String, List<ZipCodeData>> getResults() {
        return results;
    }

    public void setResults(Map<String, List<ZipCodeData>> results) {
        this.results = results;
    }
}
