package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectData {
    // Belongs to EIA dataset
    private String total;
    private String dateFormat;
    private String frequency;

    @JsonProperty("data")
    private List<ElectDataInfo> electDataInfo;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<ElectDataInfo> getElectDataInfo() {
        return electDataInfo;
    }

    public void setElectDataInfo(List<ElectDataInfo> electDataInfo) {
        this.electDataInfo = electDataInfo;
    }
}
