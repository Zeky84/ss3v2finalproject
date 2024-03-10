package finalproject.ss3v2.web;

import org.springframework.web.bind.annotation.RestController;
import finalproject.ss3v2.domain.RefreshToken;
import finalproject.ss3v2.domain.User;
import finalproject.ss3v2.service.RefreshTokenService;
import finalproject.ss3v2.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokensController {
    private RefreshTokenService refreshTokenService;


    public TokensController( RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
    @GetMapping("/newrefreshtoken")
    public ResponseEntity<String> createNewRefreshTokenIfCloseToExpire(Authentication authentication) {
        if (authentication != null) {
            Integer userId = ((User) authentication.getPrincipal()).getId();
            if (refreshTokenService.verifyRefreshTokenExpirationByUserId(userId)) {
                RefreshToken refreshToken = refreshTokenService.findByUserId(userId);
                if (refreshTokenService.isRefreshTokenAlmostExpired(refreshToken, 15)) {
                    refreshTokenService.createRefreshToken(userId);
                    return ResponseEntity.ok(" New Refresh Token created");
                }
                return ResponseEntity.ok("No new token required yet");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token expired");
    }

    @GetMapping("/refreshtokenexptime")
    public ResponseEntity<String> getRefreshTokenExpirationTime(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.findByUserId(user.getId());
        if (refreshTokenService.verifyRefreshTokenExpirationByUserId(user.getId())) {
            return ResponseEntity.ok(refreshTokenService.refreshTokenExpirationTimeLeft(refreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

    }

}
