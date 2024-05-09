package finalproject.ss3v2.repository;

import finalproject.ss3v2.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRespository extends JpaRepository<Profile, Long> {
}
