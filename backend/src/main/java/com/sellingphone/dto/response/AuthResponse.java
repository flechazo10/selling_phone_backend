package com.sellingphone.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long   expiresIn;
}
