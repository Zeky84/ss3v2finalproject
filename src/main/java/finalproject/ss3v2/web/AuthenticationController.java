package finalproject.ss3v2.web;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import finalproject.ss3v2.dao.request.RefreshTokenRequest;
import finalproject.ss3v2.dao.request.SignInRequest;
import finalproject.ss3v2.dao.response.JwtAuthenticationResponse;
import finalproject.ss3v2.dao.response.TokenRefreshResponse;
import finalproject.ss3v2.domain.RefreshToken;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.security.AuthenticationServiceImpl;
import finalproject.ss3v2.security.JwtService;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserServiceImpl;


@Controller
public class AuthenticationController {
    private final AuthenticationServiceImpl authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserServiceImpl userServiceImpl;

    public AuthenticationController(AuthenticationServiceImpl authenticationService,
                                    RefreshTokenService refreshTokenService, JwtService jwtService, UserServiceImpl userServiceImpl) {
        super();
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.userServiceImpl = userServiceImpl;
    }


    @GetMapping("/signin")
    public String getLogin(@ModelAttribute("user") User user) {
        return "signin";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "signin"; // was "login" before
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest request, @RequestBody User user) {
        Optional<User> existingUser = userServiceImpl.findUserByEmail(user.getEmail());
        String accessToken = jwtService.generateToken(user);
        return ResponseEntity.ok(authenticationService.signin(request));
    }


    /*
     * This code is from Trevor's original implementation which might be helpful for those who are not using server rendering templates
     *
     * @PostMapping("/signin") public String authenticateLogin
     * (@ModelAttribute("user") User user, SignInRequest request) {
     * Optional<User> existingUser = userService.findUserByEmail(user.getEmail());
     * User loggedUser = ((User) userService).loadUserByUsername(user.getUsername());
     * String accessToken = jwtService.generateToken(user);
     *
     * return ResponseEntity.ok(authenticationService.signin(request)); }
     */


    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new IllegalStateException(
                        "Refresh token " + requestRefreshToken + " is not in database!"));
    }
//    @PostMapping("/userexists")
//    @ResponseBody
//    public Boolean isAvailableUser(@RequestBody User user) {
//        Optional<User>  userExists= userServiceImpl.findUserByEmail(user.getEmail());
//        System.out.println(userExists.isEmpty());
//        return userExists.isPresent();
//    }
}
