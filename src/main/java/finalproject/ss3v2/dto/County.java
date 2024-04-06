package finalproject.ss3v2.dto;

public class County {
    // Attributes names are the one coming from the api.
    private String state_code;
    private String fips_code;
    private String county_name;
    private String town_name;
    private String category;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getFips_code() {
        return fips_code;
    }

    public void setFlip_code(String fips_code) {
        this.fips_code = fips_code;
    }

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
