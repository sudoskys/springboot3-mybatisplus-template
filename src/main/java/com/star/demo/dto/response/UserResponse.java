package com.star.demo.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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

    @JsonSerialize(using= ToStringSerializer.class)
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
