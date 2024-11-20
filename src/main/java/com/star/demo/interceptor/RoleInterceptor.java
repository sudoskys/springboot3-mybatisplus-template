package com.star.demo.interceptor;

import com.star.demo.annotation.RequireRole;
import com.star.demo.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequireRole annotation = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (annotation != null) {
                Optional<String> token = jwtUtil.extractToken(request);
                if (token.isEmpty()) {
                    log.warn("未认证请求被拒绝: {}", request.getRequestURI());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"未认证\"}");
                    return false;
                }
                String role = jwtUtil.extractRole(token.get());
                if (!Arrays.asList(annotation.value()).contains(role)) {
                    log.warn("权限不足, 请求被拒绝, URI: {}, role: {}", request.getRequestURI(), role);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"message\": \"权限不足\"}");
                    return false;
                }
            }
        }
        return true;
    }
}
