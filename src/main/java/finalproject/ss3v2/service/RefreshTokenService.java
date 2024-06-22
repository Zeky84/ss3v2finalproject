package finalproject.ss3v2.service;

import java.time.Duration;//GPT-4 code ... suggested to implement new methods
import java.time.Instant;//GPT-4 code ... suggested to implement new methods
import java.util.*;

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

        //----------------------------------------------------------addition to the original code
        // Retrieve all tokens for the user
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);

        // If more than one token exists, delete the older ones(just keep the latest one)
        //Note: Because i was having a list of tokens when requesting for the refresh token, i did have an error
        // when trying to get that token (many of them with the same user id), so i had to delete all the tokens before
        // the latest one
//        if (tokens.size() > 1) {
//            // Sort tokens by expiryDate
//            tokens.sort(Comparator.comparing(RefreshToken::getExpiryDate));
//
//            // Remove the newest token from the list (last item after sorting)
//            tokens.remove(tokens.size() - 1);
//
//            // Delete the older tokens
//            refreshTokenRepository.deleteAll(tokens);
//        }
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
    public Boolean verifyRefreshTokenExpirationByUserId(Integer userId) {// verify if the refresh token is not expired
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            return false;
        }
        return true;
    }
    public boolean isRefreshTokenAlmostExpired(RefreshToken refreshToken, int secondsBeforeExpiry) {//GPT-4 code help
        // Check if the refresh token is almost expired to create a new one but only if the token is not expired and it's
        // the expiration time into specific time frame(like 15 second before, how is set now for testing purposes)
        Instant expirationTime = refreshToken.getExpiryDate();
        Instant currentTime = Instant.now();
        Duration timeLeft = Duration.between(currentTime, expirationTime);
        return timeLeft.getSeconds() <= secondsBeforeExpiry && !timeLeft.isNegative();
    }
    public RefreshToken findByUserId(Integer userId) {
        // Find the refresh token by the user id, so we can check if it's expired or not
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Refresh token not found");
        }
        // Sort tokens by expiryDate
        tokens.sort(Comparator.comparing(RefreshToken::getExpiryDate));
        // Return the last token (most recent one)
        return tokens.get(tokens.size() - 1);
    }
    public String refreshTokenExpirationTimeLeft(RefreshToken refreshToken) {
        // Returns the time left until the refresh token expires
        Instant expirationTime = refreshToken.getExpiryDate();
        Instant currentTime = Instant.now();
        Duration timeLeft = Duration.between(currentTime, expirationTime);

        long minutes = timeLeft.toMinutes();
        long seconds = timeLeft.minusMinutes(minutes).getSeconds();

        return  String.format("%d:%02d", minutes, seconds);
    }
}
