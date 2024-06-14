package finalproject.ss3v2.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "profiles")
public class Profile {
    // This table attempts to capture the cost and profile description.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(name = "date_profile_created", nullable = false)
    private LocalDate dateProfileCreated = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)//FetchType.EAGER is the default
    private User user;

    @Column( nullable = false)
    private String location;

    @Column( nullable = false)
    private String stateCode;

    @Column( nullable = false)
    private String profileName;

    private Double totalCost;

    @Column(nullable = true)
    private Double rentCost;

    @Column(nullable = true)
    private Double fuelCost;

    @Column(nullable = true)
    private Double electricityCost;

    @Column(nullable = true)
    private Double wasteCost;

    @Column(nullable = true)
    private Double waterCost;

    @Column(nullable = true)
    private Double publicTransportationCost;

    @Column(nullable = true)
    private Double naturalGasCost;

    @Column(nullable = true)
    private Double internetCost;

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public LocalDate getDateProfileCreated() {
        return dateProfileCreated;
    }

    public void setDateProfileCreated(LocalDate dateProfileCreated) {
        this.dateProfileCreated = dateProfileCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
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
