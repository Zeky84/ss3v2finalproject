package finalproject.ss3v2.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "utilities_cost_by_state")
public class Utilities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String stateCode;
    private String stateName;
//    private double electricityCostCentsPerKwhThisYear;
//    private double electricityCostCentsPerKwhLastYear;
    private double gasRegularCost;
    private double gasMidGradeCost;
    private double gasPremiumCost;
    private double gasDieselCost;
//    private double estimateEnergyCost;
    private double waterCost;
    private double internetCost;
    private double naturalGasCost;
    private double trashCost;
    private double cellPhoneCost;
    private double streamingServiceCost;
    private double publicTransportationCost;

}
