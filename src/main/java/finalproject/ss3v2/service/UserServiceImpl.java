package finalproject.ss3v2.service;

import finalproject.ss3v2.domain.Authority;
import finalproject.ss3v2.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import finalproject.ss3v2.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    //All the methods related to admin control are
    // implemented in this class, and only the admin can access these methods. Each user methods are going to be
    // implemented in UserService Class

    private UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    //To delete the user we need to delete the refresh token as well
    private RefreshTokenService refreshTokenService;

    public UserServiceImpl(UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));

                List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                        .collect(Collectors.toList());

                return user;
            }
        };
    }


    @Secured({"ROLE_ADMIN"})
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    public void elevateUserToAdmin(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if the user doesn't already have the admin role
            if (user.getAuthorities().stream().noneMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))) {
                // Add the admin role to the user
                Authority adminAuthority = new Authority("ROLE_ADMIN");
                adminAuthority.setUser(user);
                user.getAuthorities().add(adminAuthority);

                logger.info("Setting Auth for user: " + user.getId() + user.getEmail());
                logger.info("Setting Authorities: " + user.getAuthorities());

                // Save the updated user
                user.setAdmin(true);
                userRepository.save(user);
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Secured("ROLE_ADMIN")// add to the original code
    @Transactional
    public void removeAdminPrivileges(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            // Check if the user has the admin role
            Authority adminAuthority = user.getAuthorities().stream()
                    .filter(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))
                    .findFirst()
                    .orElse(null);

            if (adminAuthority != null) {
                // Remove the admin role from the user
                user.getAuthorities().remove(adminAuthority);
                adminAuthority.setUser(null);

                user.setAdmin(false);
                userRepository.save(user);

                logger.info("Removed Admin Role for user: " + user.getId() + " " + user.getEmail());
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Secured("ROLE_ADMIN")// add to the original code
    @Transactional
    public void elevateUserToSuperUser(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // BY ME
            if (user.getAuthorities().stream().noneMatch(auth -> "ROLE_SUPERUSER".equals(auth.getAuthority()))) {
                // Add the superuser role to the user
                Authority superUserAuth = new Authority("ROLE_SUPERUSER");
                superUserAuth.setUser(user);
                user.getAuthorities().add(superUserAuth);

                logger.info("Setting Auth for user: " + user.getId() + user.getEmail());
                logger.info("Setting Authorities: " + user.getAuthorities());

                user.setSuperUser(true);
                // Save the updated user
                userRepository.save(user);
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Secured("ROLE_ADMIN")// add to the original code
    @Transactional
    public void removeSuperUserPrivileges(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);


        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            Authority superUserAuth = user.getAuthorities().stream()
                    .filter(auth -> "ROLE_SUPERUSER".equals(auth.getAuthority()))
                    .findFirst()
                    .orElse(null);

            if (superUserAuth != null) {
                // Remove the admin role from the user
                user.getAuthorities().remove(superUserAuth);
                superUserAuth.setUser(null);

                user.setSuperUser(false);
                userRepository.save(user);

                logger.info("Removed Admin Role for user: " + user.getId() + " " + user.getEmail());
            }
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    @Secured("ROLE_ADMIN")// add to the original code
    @Transactional
    public void deleteUser(Integer userId) {
        refreshTokenService.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return null;
        }
        return userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
