package finalproject.ss3v2.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
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


}
