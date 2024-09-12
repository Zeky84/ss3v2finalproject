package finalproject.ss3v2.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;



public class TestingProfile {
    // This class is used to create profiles for testing purposes, User will test app functionality without having to
    // sign up in the app.

    private String location;

    private String stateCode;

    private Double totalCost;


    private Double rentCost;


    private Double fuelCost;


    private Double electricityCost;


    private Double wasteCost;


    private Double waterCost;


    private Double publicTransportationCost;


    private Double naturalGasCost;


    private Double internetCost;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getRentCost() {
        return rentCost;
    }

    public void setRentCost(Double rentCost) {
        this.rentCost = rentCost;
    }

    public Double getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(Double fuelCost) {
        this.fuelCost = fuelCost;
    }

    public Double getElectricityCost() {
        return electricityCost;
    }

    public void setElectricityCost(Double electricityCost) {
        this.electricityCost = electricityCost;
    }

    public Double getWasteCost() {
        return wasteCost;
    }

    public void setWasteCost(Double wasteCost) {
        this.wasteCost = wasteCost;
    }

    public Double getWaterCost() {
        return waterCost;
    }

    public void setWaterCost(Double waterCost) {
        this.waterCost = waterCost;
    }

    public Double getPublicTransportationCost() {
        return publicTransportationCost;
    }

    public void setPublicTransportationCost(Double publicTransportationCost) {
        this.publicTransportationCost = publicTransportationCost;
    }

    public Double getNaturalGasCost() {
        return naturalGasCost;
    }

    public void setNaturalGasCost(Double naturalGasCost) {
        this.naturalGasCost = naturalGasCost;
    }

    public Double getInternetCost() {
        return internetCost;
    }

    public void setInternetCost(Double internetCost) {
        this.internetCost = internetCost;
    }
}
