package com.star.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import com.star.demo.common.ApiResponse;
import com.star.demo.common.ErrorCode;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiResponse<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        log.error("邮箱已存在: {}", ex.getMessage());
        return ApiResponse.error(
            ex.getErrorMessage(),
            ErrorCode.EMAIL_ALREADY_EXISTS.getCode()
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse<?> handleInvalidPasswordException(InvalidPasswordException ex) {
        log.error("密码不正确: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ErrorCode.INVALID_PASSWORD.getCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ApiResponse.error(
            "输入验证失败",
            ErrorCode.PARAM_ERROR.getCode(),
            errors
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse<?> handleAllExceptions(Exception ex) {
        log.error("系统错误", ex);
        return ApiResponse.error(
            "系统内部错误",
            ErrorCode.SYSTEM_ERROR.getCode()
        );
    }
}
