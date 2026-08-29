package com.sellingphone.mapper;

import com.sellingphone.dto.response.UserResponse;
import com.sellingphone.entity.User;
import org.springframework.stereotype.Component;

/**
 * UserMapper: chuyển đổi User entity → UserResponse DTO.
 * Tầng mapper đảm bảo Controller/Service không trả về entity trực tiếp.
 */
@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(String.valueOf(user.getUserId()));
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setFullName(user.getFullName());
        response.setAvatar(user.getAvatar());
        response.setGender(user.getGender());
        response.setRoleName(user.getRole() != null ? user.getRole().getName() : null);
        return response;
    }
}
