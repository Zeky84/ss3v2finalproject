package finalproject.ss3v2.repository;

import finalproject.ss3v2.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {


    @Modifying
    @Query("delete from Authority a where a.user.id = :id")
    public void deleteByUserId(Integer id);
}
