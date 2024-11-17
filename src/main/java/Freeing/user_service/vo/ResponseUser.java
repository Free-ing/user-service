package Freeing.user_service.vo;

import lombok.Data;

@Data
public class ResponseUser {
    private String email;
    private String name;
    private Long userId;
    private int role;
}
