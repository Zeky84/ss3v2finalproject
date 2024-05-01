package finalproject.ss3v2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "utilities_cost_by_state")
@AllArgsConstructor// from lombok
@NoArgsConstructor // from lombok
@Data // from lombok
@EqualsAndHashCode // from lombok
@SuperBuilder // from lombok
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

}
