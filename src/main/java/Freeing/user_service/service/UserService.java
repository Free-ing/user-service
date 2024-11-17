package Freeing.user_service.service;

import Freeing.user_service.dto.UserDto;
import Freeing.user_service.repository.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDto createUser(UserDto userDto);

    boolean checkEmail(String email);
    UserDto getUserByUserId(Long userId);
    Iterable<UserEntity> getUserByAll();

    UserEntity findByEmail(String email);
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    boolean setNewPassword(String email, String newPassword);

    void deleteUser(Long userId);

}
