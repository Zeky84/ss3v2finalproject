package finalproject.ss3v2.service;

import finalproject.ss3v2.domain.Authority;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.repository.AuthorityRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {
    AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    // The methods below are created to display the buttons that allows the admin to change the role of a user
    public boolean isNotAdmin(User user){
        return user.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    public boolean isNotSuperUser(User user){
        return user.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_SUPERUSER"));
    }

}
