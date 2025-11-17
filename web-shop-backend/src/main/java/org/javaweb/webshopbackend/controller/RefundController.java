package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.Refund;
import org.javaweb.webshopbackend.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 退款Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/refund")
@Tag(name = "退款管理", description = "退款相关接口")
public class RefundController {

    @Autowired
    private RefundService refundService;

    /**
     * 提交退款申请
     */
    @PostMapping
    @Operation(summary = "提交退款申请", description = "用户提交退款申请")
    public Result<Void> applyRefund(@RequestBody Refund refund) {
        log.info("提交退款申请：orderId={}, amount={}", refund.getOrderId(), refund.getRefundAmount());

        refundService.applyRefund(refund);

        return Result.success("退款申请已提交");
    }

    /**
     * 商家审核退款申请
     */
    @PutMapping("/{refundId}/review")
    @Operation(summary = "商家审核退款申请", description = "商家审核退款申请（同意/拒绝）")
    public Result<Void> reviewRefund(
            @Parameter(description = "退款ID", required = true, example = "1")
            @PathVariable Long refundId,
            @Parameter(description = "审核状态（1-同意，2-拒绝）", required = true, example = "1")
            @RequestParam Integer status) {
        log.info("商家审核退款：refundId={}, status={}", refundId, status);

        refundService.reviewRefund(refundId, status);

        return Result.success("审核完成");
    }

    /**
     * 取消退款申请
     */
    @PutMapping("/{refundId}/cancel")
    @Operation(summary = "取消退款申请", description = "用户取消退款申请")
    public Result<Void> cancelRefund(
            @Parameter(description = "退款ID", required = true, example = "1")
            @PathVariable Long refundId,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId) {
        log.info("取消退款申请：refundId={}, userId={}", refundId, userId);

        refundService.cancelRefund(refundId, userId);

        return Result.success("退款申请已取消");
    }

    /**
     * 分页查询退款申请列表（管理员）
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询退款申请列表", description = "管理员查询所有退款申请")
    public Result<IPage<Refund>> getRefundList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "退款状态（0-待审核，1-已同意，2-已拒绝）", example = "0")
            @RequestParam(required = false) Integer status) {
        log.info("分页查询退款申请列表：pageNum={}, pageSize={}, status={}", pageNum, pageSize, status);

        Page<Refund> page = new Page<>(pageNum, pageSize);
        IPage<Refund> refundPage = refundService.getRefundPage(page, status);

        return Result.success(refundPage);
    }

    /**
     * 获取用户的退款申请列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的退款申请列表", description = "用户查看自己的退款申请")
    public Result<IPage<Refund>> getUserRefunds(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId,
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户的退款申请列表：userId={}", userId);

        Page<Refund> page = new Page<>(pageNum, pageSize);
        IPage<Refund> refundPage = refundService.getUserRefunds(userId, page);

        return Result.success(refundPage);
    }

    /**
     * 获取退款详情
     */
    @GetMapping("/{refundId}")
    @Operation(summary = "获取退款详情", description = "获取退款申请的详细信息（含订单信息）")
    public Result<Refund> getRefundDetail(
            @Parameter(description = "退款ID", required = true, example = "1")
            @PathVariable Long refundId) {
        log.info("获取退款详情：refundId={}", refundId);

        Refund refund = refundService.getRefundDetail(refundId);

        return Result.success(refund);
    }

    /**
     * 完成退款
     */
    @PutMapping("/{refundId}/complete")
    @Operation(summary = "完成退款", description = "管理员完成退款处理")
    public Result<Void> completeRefund(
            @Parameter(description = "退款ID", required = true, example = "1")
            @PathVariable Long refundId) {
        log.info("完成退款：refundId={}", refundId);

        refundService.completeRefund(refundId);

        return Result.success("退款已完成");
    }

    /**
     * 根据订单ID获取退款信息
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "根据订单ID获取退款信息", description = "获取指定订单的退款申请")
    public Result<Refund> getRefundByOrderId(
            @Parameter(description = "订单ID", required = true, example = "1")
            @PathVariable Long orderId) {
        log.info("根据订单ID获取退款信息：orderId={}", orderId);

        Refund refund = refundService.getRefundByOrderId(orderId);

        return Result.success(refund);
    }
}

