package Freeing.user_service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private Long userId;
    private String email;
    private String name;
    private String password;
    private int role;

    private String encryptedPassword;
}
