package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRent {
    // Belongs to Hud User Data
    @JsonProperty("county_name")
    private String countyName;
    @JsonProperty("counties_msa")
    private String countiesMsa;
    @JsonProperty("town_name")
    private String townName;
    @JsonProperty("metro_status")
    private String metroStatus;
    @JsonProperty("metro_name")
    private String metroName;
    @JsonProperty("area_name")
    private String areaName;
    @JsonProperty("smallarea_status")
    private String smallAreaStatus;
    private List<BasicData> basicdata;

    private String year; // sometimes appear, sometimes not

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountiesMsa() {
        return countiesMsa;
    }

    public void setCountiesMsa(String countiesMsa) {
        this.countiesMsa = countiesMsa;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getMetroStatus() {
        return metroStatus;
    }

    public void setMetroStatus(String metroStatus) {
        this.metroStatus = metroStatus;
    }

    public String getMetroName() {
        return metroName;
    }

    public void setMetroName(String metroName) {
        this.metroName = metroName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSmallAreaStatus() {
        return smallAreaStatus;
    }

    public void setSmallAreaStatus(String smallAreaStatus) {
        this.smallAreaStatus = smallAreaStatus;
    }

    public List<BasicData> getBasicdata() {
        return basicdata;
    }

    public void setBasicdata(List<BasicData> basicdata) {
        this.basicdata = basicdata;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
