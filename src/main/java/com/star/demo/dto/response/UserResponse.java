package com.star.demo.dto.response;

import com.star.demo.model.User;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String role;

    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .role(user.getRole().toString())
            .build();
    }
}
