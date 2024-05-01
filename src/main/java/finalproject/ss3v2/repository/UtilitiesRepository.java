package finalproject.ss3v2.repository;

import finalproject.ss3v2.domain.Utilities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilitiesRepository extends JpaRepository<Utilities, Long> {
    public Utilities findByStateCode(String stateCode);
}
