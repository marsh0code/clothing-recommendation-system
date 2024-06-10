package com.example.wardrobeapi.web.dto.auth;

import lombok.Data;

@Data
public class JwtToken {
    private String token;
    private String type;
    private String algorithm;
    private Long expiresAt;
}
