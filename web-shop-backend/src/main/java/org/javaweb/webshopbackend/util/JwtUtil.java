package org.javaweb.webshopbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和验证 JWT Token
 * 支持随机密钥生成,提高安全性
 *
 * @author WebShop Team
 * @date 2025-11-11
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.use-random-secret:false}")
    private boolean useRandomSecret;

    private SecretKey secretKey;

    /**
     * 初始化JWT密钥
     * 如果启用随机密钥,则在应用启动时生成随机密钥
     * 否则使用配置文件中的固定密钥
     */
    @PostConstruct
    public void init() {
        if (useRandomSecret) {
            // 生成256位(32字节)的随机密钥
            this.secretKey = generateRandomSecretKey();
            log.info("JWT使用随机密钥模式 - 密钥已生成");
            log.warn("注意: 使用随机密钥模式时,应用重启后之前生成的所有Token将失效");
        } else {
            if (secret == null || secret.isEmpty()) {
                throw new IllegalStateException("JWT密钥未配置! 请在application.properties中设置jwt.secret或启用jwt.use-random-secret");
            }
            // 使用配置的固定密钥
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            log.info("JWT使用固定密钥模式");
        }
    }

    /**
     * 生成随机密钥
     *使用SecureRandom生成高强度随机密钥
     */
    private SecretKey generateRandomSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256位密钥
        secureRandom.nextBytes(keyBytes);
        // 记录密钥的Base64编码(仅用于调试,生产环境应移除)
        String keyBase64 = Base64.getEncoder().encodeToString(keyBytes);
        log.debug("生成的随机密钥(Base64): {}", keyBase64);
        
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSignKey() {
        return secretKey;
    }

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date issuedAt = new Date();
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);
        
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSignKey())
                .compact();
        
        log.info("JWT Token生成成功 - 用户: {}, 角色: {}, 过期时间: {}", username, role, expirationDate);
        log.debug("生成的Token: {}", token);
        
        return token;
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 中获取 Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从 Token 中获取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}