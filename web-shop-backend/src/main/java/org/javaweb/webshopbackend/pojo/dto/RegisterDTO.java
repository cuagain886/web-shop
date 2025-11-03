package org.javaweb.webshopbackend.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户注册DTO
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterDTO {

    @Schema(description = "用户名", example = "newuser")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "昵称", example = "小明")
    private String nickname;

    @Schema(description = "邮箱", example = "user@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "头像URL", example = "/images/avatars/default.jpg")
    private String avatar;

    @Schema(description = "性别（0-女，1-男，2-保密）", example = "1")
    private Integer gender;

    @Schema(description = "生日", example = "1995-08-15")
    private LocalDate birthday;
}

