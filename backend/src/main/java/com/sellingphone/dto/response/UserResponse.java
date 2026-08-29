package com.sellingphone.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String avatar;
    private String gender;
    private String roleName;
}
