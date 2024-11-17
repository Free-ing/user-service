package Freeing.user_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import Freeing.user_service.repository.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserEmail(String userEmail);
    void deleteByUserEmail(String userEmail);
    RefreshToken findByUser_UserId(Long userId);
    void deleteByUserUserId(Long userId);
}
