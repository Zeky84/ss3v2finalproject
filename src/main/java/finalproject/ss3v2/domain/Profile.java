package finalproject.ss3v2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.YearMonth;

@Entity(name = "profiles")
@AllArgsConstructor// from lombok
@NoArgsConstructor // from lombok
@Data // from lombok
@EqualsAndHashCode // from lombok
@SuperBuilder // from lombok
public class Profile {
    // This table attempts to capture the cost and profile description.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(name = "date_profile_created", nullable = false)
    private LocalDate dateProfileCreated = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "user_id")//FetchType.EAGER is the default
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


}
