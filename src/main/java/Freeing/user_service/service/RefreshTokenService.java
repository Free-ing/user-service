package Freeing.user_service.service;


import Freeing.user_service.repository.RefreshToken;
import Freeing.user_service.repository.RefreshTokenRepository;
import Freeing.user_service.repository.UserEntity;
import Freeing.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private UserRepository userRepository;

    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    private final long REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60; // 7일

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteExpiredToken(RefreshToken token) {
        if (isTokenExpired(token)) {
            refreshTokenRepository.delete(token);
        }
    }
    public void updateRefreshToken(String userEmail, String newToken, LocalDateTime expiryDate, int role) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserEmail(userEmail);
        if (existingToken.isPresent()) {
            RefreshToken refreshToken = existingToken.get();
            refreshToken.setToken(newToken);
            refreshToken.setExpiryDate(expiryDate);
            saveRefreshToken(refreshToken);
        } else {
            // 새로운 토큰을 만들 때 UserEntity에서 userId 값을 가져오도록 보장해야 함.
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(newToken);
            refreshToken.setUser(user); // UserEntity 객체를 사용하여 연결
            refreshToken.setRole(role);
            refreshToken.setExpiryDate(expiryDate);
            saveRefreshToken(refreshToken);
        }
    }



    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }
}
