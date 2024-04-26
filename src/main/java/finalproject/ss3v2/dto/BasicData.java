package finalproject.ss3v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicData {
    // Belongs to Hud User Data

    @JsonProperty("zip_code")// sometimes appear, sometimes not
    private String zipCode;
    @JsonProperty("Efficiency")
    private int efficiency;
    @JsonProperty("One-Bedroom")
    private int oneBedroom;
    @JsonProperty("Two-Bedroom")
    private int twoBedroom;
    @JsonProperty("Three-Bedroom")
    private int threeBedroom;
    @JsonProperty("Four-Bedroom")
    private int fourBedroom;

    private String year;

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getOneBedroom() {
        return oneBedroom;
    }

    public void setOneBedroom(int oneBedroom) {
        this.oneBedroom = oneBedroom;
    }

    public int getTwoBedroom() {
        return twoBedroom;
    }

    public void setTwoBedroom(int twoBedroom) {
        this.twoBedroom = twoBedroom;
    }

    public int getThreeBedroom() {
        return threeBedroom;
    }

    public void setThreeBedroom(int threeBedroom) {
        this.threeBedroom = threeBedroom;
    }

    public int getFourBedroom() {
        return fourBedroom;
    }

    public void setFourBedroom(int fourBedroom) {
        this.fourBedroom = fourBedroom;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
