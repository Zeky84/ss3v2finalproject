package finalproject.ss3v2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "api_huduser_states")
@AllArgsConstructor// from lombok
@NoArgsConstructor // from lombok
@Data // from lombok
@EqualsAndHashCode // from lombok
@SuperBuilder // from lombok
public class ApiState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //In Java, Trevor told me once isn't a good practice to use underscore in variable names like I learned in python
    //but to store the data directly into the db i need to this adjustment cause the date in the json is in snake case,
    // so need to match the names
    private Long id;
    private String state_name;
    private String state_code;
    private String state_num;
    private String category;
}
