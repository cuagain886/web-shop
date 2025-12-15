package org.javaweb.webshopbackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 *用于生成BCrypt加密的密码哈希值
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成"123456"的BCrypt哈希
        String password = "123456";
        String hash = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println();
        
        // 验证生成的哈希是否正确
        boolean matches = encoder.matches(password, hash);
        System.out.println("验证结果: " + (matches ? "✓ 成功" : "✗ 失败"));
        System.out.println();
        
        // 生成SQL更新语句
        System.out.println("SQL更新语句:");
        System.out.println("UPDATE user SET password = '" + hash + "', updated_time = CURRENT_TIMESTAMP WHERE username = 'testM';");
    }
}