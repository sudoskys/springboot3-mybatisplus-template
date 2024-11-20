package com.star.demo.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS("200", "成功"),
    PARAM_ERROR("400", "参数错误"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "禁止访问"),
    NOT_FOUND("404", "资源不存在"),
    EMAIL_ALREADY_EXISTS("USER_001", "邮箱已被注册"),
    INVALID_PASSWORD("USER_002", "密码格式无效"),
    SYSTEM_ERROR("500", "系统内部错误");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
