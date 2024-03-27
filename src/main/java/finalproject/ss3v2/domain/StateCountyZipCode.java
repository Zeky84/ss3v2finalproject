package finalproject.ss3v2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "state_county_zip_code")
public class StateCountyZipCode {
    @Id // this id is going to be set with the id value from the state info api
    private Long id;
}
