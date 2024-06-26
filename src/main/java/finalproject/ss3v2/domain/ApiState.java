package finalproject.ss3v2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "api_huduser_states")
public class ApiState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    @JsonProperty("state_name")
    private String stateName;

    @JsonProperty("state_code")
    private String stateCode;

    @JsonProperty("state_num")
    private String stateNum;

    private String category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateNum() {
        return stateNum;
    }

    public void setStateNum(String stateNum) {
        this.stateNum = stateNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
