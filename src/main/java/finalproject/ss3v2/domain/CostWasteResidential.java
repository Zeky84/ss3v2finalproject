package finalproject.ss3v2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "costWasteResidential")
@AllArgsConstructor// from lombok
@NoArgsConstructor // from lombok
@Data // from lombok
@EqualsAndHashCode // from lombok
@SuperBuilder // from lombok
public class CostWasteResidential {
    @Id
    private Long profileId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "profileId")
    private Profile profile;

    @Column(name = "waste_cost")
    private Double wasteCost;
}
