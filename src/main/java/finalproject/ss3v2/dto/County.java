package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class County {
    // Belongs to Hud User Data

    @JsonProperty("state_code")
    private String stateCode;
    @JsonProperty("fips_code")
    private String fipsCode;
    @JsonProperty("county_name")
    private String countyName;
    @JsonProperty("town_name")
    private String townName;

    private String category;

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getFipsCode() {
        return fipsCode;
    }

    public void setFipsCode(String fipsCode) {
        this.fipsCode = fipsCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
