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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(name="profile_name", nullable = false)
    private String profileName;

    @Column(name="date_profile_created", nullable = false)
    private LocalDate dateProfileCreated;

    @Column(name = "date_requested", nullable = false)
    private YearMonth yearMonthInfoRequested;

    @Column(name="state_or_region", nullable = false)
    private String stateOrRegion;

    @Column(name = "zip_code", nullable = false)
    private Integer zipCode;

    private String address;

    @Column(name = "house_hold_size")// could be null, in that case is going to be evaluated as 1
    private Integer houseHoldSize;

    @ManyToOne
    @JoinColumn(name = "user_id")//FetchType.EAGER is the default
    private User user;

    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)//FetchType.EAGER is the default
    private CostElectricityResidential electricity;

    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
    private CostWaterResidential water;

    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
    private CostWasteResidential waste;

    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
    private CostRent rent;

    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
    private CostFuel fuel;



}
