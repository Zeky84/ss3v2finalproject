package finalproject.ss3v2.repository;

import finalproject.ss3v2.domain.ApiState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiStateRepository extends JpaRepository<ApiState, Long> {

}
