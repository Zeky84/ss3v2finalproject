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
    // This table attempts to capture only the cost. The user needs to describe the profile, the variables involve in
    // this table will display in the future which variables the user chose to include in the profile. but for now just
    // the total cost of the adds of all the variables.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(name="profile_name", nullable = false)
    private String profileName;

    @Column(name="date_profile_created", nullable = false)
    private LocalDate dateProfileCreated;


    private String profileDescription;//this is a description of the profile()


    @ManyToOne
    @JoinColumn(name = "user_id")//FetchType.EAGER is the default
    private User user;

//    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)//FetchType.EAGER is the default
//    private CostElectricityResidential electricity;
//
//    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
//    private CostWaterResidential water;
//
//    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
//    private CostWasteResidential waste;
//
//    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
//    private CostRent rent;
//
//    @OneToOne(mappedBy = "profile",cascade = {CascadeType.PERSIST, CascadeType.REMOVE},orphanRemoval = true)
//    private CostFuel fuel;



}
