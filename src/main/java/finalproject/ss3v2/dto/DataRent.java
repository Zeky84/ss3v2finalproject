package finalproject.ss3v2.dto;

public class DataRent {
    //attributes are the one coming from the API
    private String county_name;
    private String counties_msa;
    private String town_name;
    private String metro_status;
    private String metro_name;
    private String area_name;
    private String smallarea_status;
    private BasicData basicdata; // nested object

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }

    public String getCounties_msa() {
        return counties_msa;
    }

    public void setCounties_msa(String counties_msa) {
        this.counties_msa = counties_msa;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }

    public String getMetro_status() {
        return metro_status;
    }

    public void setMetro_status(String metro_status) {
        this.metro_status = metro_status;
    }

    public String getMetro_name() {
        return metro_name;
    }

    public void setMetro_name(String metro_name) {
        this.metro_name = metro_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getSmallarea_status() {
        return smallarea_status;
    }

    public void setSmallarea_status(String smallarea_status) {
        this.smallarea_status = smallarea_status;
    }

    public BasicData getBasicdata() {
        return basicdata;
    }

    public void setBasicdata(BasicData basicdata) {
        this.basicdata = basicdata;
    }
}
