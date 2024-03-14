package finalproject.ss3v2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import finalproject.ss3v2.domain.RefreshToken;
import finalproject.ss3v2.domain.User;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);

    //-------------------------------------------------------------------------addition to the original code
    Optional<RefreshToken> findByUserId(Integer userId);

    List<RefreshToken> findAllByUserId(Integer userId);

    RefreshToken findByUser(User user);
}
