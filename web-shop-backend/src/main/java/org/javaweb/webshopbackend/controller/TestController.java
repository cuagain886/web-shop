package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证后端配置是否正常工作
 * 
 * @author WebShop Team
 * @date 2025-11-02
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Tag(name = "测试接口", description = "用于测试后端配置的接口")
public class TestController {

    /**
     * Hello World 测试
     * 验证基本接口是否可访问
     */
    @GetMapping("/hello")
    @Operation(summary = "Hello World 测试", description = "返回欢迎消息")
    public Result<String> hello() {
        log.info("测试接口被调用：/test/hello");
        return Result.success("欢迎使用 WebShop 电商平台后端 API！");
    }

    /**
     * 参数测试
     * 验证参数接收和返回
     */
    @GetMapping("/echo")
    @Operation(summary = "参数回显测试", description = "返回接收到的参数")
    public Result<Map<String, Object>> echo(@RequestParam(required = false) String message) {
        log.info("收到参数：message={}", message);
        
        Map<String, Object> data = new HashMap<>();
        data.put("receivedMessage", message);
        data.put("timestamp", LocalDateTime.now());
        data.put("status", "success");
        
        return Result.success("参数接收成功", data);
    }

    /**
     * 时间格式测试
     * 验证 JSON 时间序列化配置
     */
    @GetMapping("/time")
    @Operation(summary = "时间格式测试", description = "返回当前时间，验证时间格式化")
    public Result<Map<String, Object>> time() {
        Map<String, Object> data = new HashMap<>();
        data.put("currentTime", LocalDateTime.now());
        data.put("timestamp", System.currentTimeMillis());
        
        log.info("返回当前时间：{}", LocalDateTime.now());
        return Result.success("时间获取成功", data);
    }

    /**
     * 异常测试
     * 验证全局异常处理
     */
    @GetMapping("/error")
    @Operation(summary = "异常测试", description = "触发异常，验证全局异常处理")
    public Result<String> error(@RequestParam(defaultValue = "runtime") String type) {
        log.warn("触发异常测试，类型：{}", type);
        
        switch (type) {
            case "runtime":
                throw new RuntimeException("这是一个运行时异常测试");
            case "illegal":
                throw new IllegalArgumentException("这是一个非法参数异常测试");
            case "null":
                // 故意触发空指针异常，用于测试全局异常处理
                return triggerNullPointerException();
            default:
                return Result.error("未知的异常类型");
        }
    }
    
    /**
     * 触发空指针异常的辅助方法
     * 用于测试全局异常处理
     */
    @SuppressWarnings("null")
    private Result<String> triggerNullPointerException() {
        String str = null;
        //noinspection ConstantConditions,DataFlowIssue
        return Result.success(str.length() + "");
    }

    /**
     * 日志测试
     * 验证日志配置
     */
    @GetMapping("/log")
    @Operation(summary = "日志测试", description = "输出不同级别的日志")
    public Result<String> log() {
        log.debug("这是 DEBUG 级别的日志");
        log.info("这是 INFO 级别的日志");
        log.warn("这是 WARN 级别的日志");
        log.error("这是 ERROR 级别的日志");
        
        return Result.success("日志已输出到控制台");
    }

    /**
     * 健康检查
     * 用于监控和负载均衡
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "返回服务健康状态")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "WebShop Backend");
        data.put("version", "1.0.0");
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success("服务运行正常", data);
    }
}

