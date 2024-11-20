package com.star.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("users")
public class User {
    public enum Role {
        USER, ADMIN
    }

    private Long id;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少为6个字符")
    private String password;

    @Email(message = "请输入有效的邮箱地址")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotNull(message = "角色不能为空")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    // 默认角色为 USER
}
