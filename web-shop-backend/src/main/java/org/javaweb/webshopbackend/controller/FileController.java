package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@Tag(name = "文件上传", description = "文件上传相关接口")
public class FileController {

    @Value("${file.upload.path:./uploads/image}")
    private String uploadPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @PostMapping("/image")
    @Operation(summary = "上传图片", description = "上传商品图片")
    public Result<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "prefix", required = false) String prefix) {
        log.info("上传图片：filename={}, size={}, prefix={}", file.getOriginalFilename(), file.getSize(), prefix);

        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        try {
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            
            // 如果提供了前缀，使用前缀_UUID，否则只用UUID
            String filename;
            if (prefix != null && !prefix.trim().isEmpty()) {
                // 清理前缀，移除特殊字符
                String cleanPrefix = prefix.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5-]", "_");
                filename = cleanPrefix + "_" + UUID.randomUUID().toString() + extension;
            } else {
                filename = UUID.randomUUID().toString() + extension;
            }

            // 保存文件
            Path filePath = Paths.get(uploadPath, filename);
            Files.write(filePath, file.getBytes());

            // 返回访问URL（使用相对路径，适配Docker部署和nginx代理）
            String url = "/uploads/image/" + filename;
            Map<String, String> result = new HashMap<>();
            result.put("url", url);

            log.info("图片上传成功：url={}", url);
            return Result.success("上传成功", result);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}