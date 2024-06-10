package com.example.wardrobeapi.web.dto.user;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String imageUrl;
    private Long createdAt;
}
