package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectData {
    // Belongs to EIA dataset
    private String total;
    private String dateFormat;
    private String monthly;

    @JsonProperty("data")
    private String electDataInfo;

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

    public String getMonthly() {
        return monthly;
    }

    public void setMonthly(String monthly) {
        this.monthly = monthly;
    }

    public String getElectDataInfo() {
        return electDataInfo;
    }

    public void setElectDataInfo(String electDataInfo) {
        this.electDataInfo = electDataInfo;
    }
}
