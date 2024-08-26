package finalproject.ss3v2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "utilities_cost_by_state")
public class Utilities {
    // This Db will store the values from the .csv file. In this Db aren't included the rent cost and the electricity
    // rate cause these values are going to be provided by APIs calls.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String stateCode;
    private String stateName;
    private double gasRegularCost;
    private double gasMidGradeCost;
    private double gasPremiumCost;
    private double gasDieselCost;
    private double monthlyWaterCost;
    private double monthlyInternetCost;
    private double monthlyNaturalGasCost;
    private double monthlyWasteCost;
    private double publicTransportationCost;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public double getGasRegularCost() {
        return gasRegularCost;
    }

    public void setGasRegularCost(double gasRegularCost) {
        this.gasRegularCost = gasRegularCost;
    }

    public double getGasMidGradeCost() {
        return gasMidGradeCost;
    }

    public void setGasMidGradeCost(double gasMidGradeCost) {
        this.gasMidGradeCost = gasMidGradeCost;
    }

    public double getGasPremiumCost() {
        return gasPremiumCost;
    }

    public void setGasPremiumCost(double gasPremiumCost) {
        this.gasPremiumCost = gasPremiumCost;
    }

    public double getGasDieselCost() {
        return gasDieselCost;
    }

    public void setGasDieselCost(double gasDieselCost) {
        this.gasDieselCost = gasDieselCost;
    }

    public double getMonthlyWaterCost() {
        return monthlyWaterCost;
    }

    public void setMonthlyWaterCost(double monthlyWaterCost) {
        this.monthlyWaterCost = monthlyWaterCost;
    }

    public double getMonthlyInternetCost() {
        return monthlyInternetCost;
    }

    public void setMonthlyInternetCost(double monthlyInternetCost) {
        this.monthlyInternetCost = monthlyInternetCost;
    }

    public double getMonthlyNaturalGasCost() {
        return monthlyNaturalGasCost;
    }

    public void setMonthlyNaturalGasCost(double monthlyNaturalGasCost) {
        this.monthlyNaturalGasCost = monthlyNaturalGasCost;
    }

    public double getMonthlyWasteCost() {
        return monthlyWasteCost;
    }

    public void setMonthlyWasteCost(double monthlyWasteCost) {
        this.monthlyWasteCost = monthlyWasteCost;
    }

    public double getPublicTransportationCost() {
        return publicTransportationCost;
    }

    public void setPublicTransportationCost(double publicTransportationCost) {
        this.publicTransportationCost = publicTransportationCost;
    }
}
