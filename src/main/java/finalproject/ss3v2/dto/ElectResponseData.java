package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectResponseData {
    @JsonProperty("response")
    private ElectData electData;

    public ElectData getElectData() {
        return electData;
    }

    public void setElectData(ElectData electData) {
        this.electData = electData;
    }
}
