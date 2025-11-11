package org.javaweb.webshopbackend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 拦截器
 * 验证请求中的 JWT Token
 * 
 * @author WebShop Team
 * @date 2025-11-11
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取 Token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 Token
        if (token == null || token.isEmpty()) {
            log.warn("请求未携带 Token: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "未授权，请先登录");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 验证失败: {}", request.getRequestURI());
            sendUnauthorizedResponse(response, "Token 无效或已过期");
            return false;
        }

        // 将用户信息存入请求属性
        try {
            Claims claims = jwtUtil.getClaimsFromToken(token);
            request.setAttribute("userId", claims.get("userId", Long.class));
            request.setAttribute("username", claims.get("username", String.class));
            request.setAttribute("role", claims.get("role", String.class));
            
            log.debug("Token 验证成功: userId={}, username={}", 
                    claims.get("userId"), claims.get("username"));
        } catch (Exception e) {
            log.error("解析 Token 失败", e);
            sendUnauthorizedResponse(response, "Token 解析失败");
            return false;
        }

        return true;
    }

    /**
     * 发送未授权响应
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}