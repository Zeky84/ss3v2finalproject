package finalproject.ss3v2.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import finalproject.ss3v2.dao.request.RefreshTokenRequest;
import finalproject.ss3v2.domain.RefreshToken;
import finalproject.ss3v2.repository.RefreshTokenRepository;
import finalproject.ss3v2.repository.UserRepository;
import finalproject.ss3v2.security.JwtServiceImpl;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {
    @Value("${token.refreshExpiration}")
    private Long refreshTokenDurationMs;

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private JwtServiceImpl jwtService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
                               JwtServiceImpl jwtService) {
        super();
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Integer userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);

            throw new IllegalStateException(
                    "Refresh token " + token.getToken() + " was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Integer userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    public String createNewAccessToken(RefreshTokenRequest refreshTokenRequest) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshTokenRequest.refreshToken());
        String accessToken = refreshTokenOpt
                .map(this::verifyExpiration)
                .map(refreshToken -> jwtService.generateToken(new HashMap<>(), refreshToken.getUser()))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        return accessToken;
    }

    //--------------------------------------------------------------------------------addition to the original code

    public List<RefreshToken> findByUser(Integer userId) {// getting all refresh tokens for a user
        // Because there is list of refresh tokens for a user, we need to get the last one
        return refreshTokenRepository.findAllByUserId(userId);
    }

    public Boolean verifyRefreshTokenExpirationByUserId(Integer userId) {// verify if the last refresh token is not expired
        List<RefreshToken> refreshTokenList = findByUser(userId);
        RefreshToken refreshToken = refreshTokenList.get(refreshTokenList.size()-1);
        System.out.println(refreshToken.getExpiryDate());//for debugging
        System.out.println(Instant.now());//for debugging
        if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            return false;
        }
        return true;
    }
}
