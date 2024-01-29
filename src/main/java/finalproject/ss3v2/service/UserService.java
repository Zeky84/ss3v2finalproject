package finalproject.ss3v2.service;

import finalproject.ss3v2.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    List<User> findAll();
}
