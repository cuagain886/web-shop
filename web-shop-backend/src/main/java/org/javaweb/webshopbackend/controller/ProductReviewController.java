package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.ProductReview;
import org.javaweb.webshopbackend.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评价Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/review")
@Tag(name = "商品评价管理", description = "商品评价相关接口")
public class ProductReviewController {

    @Autowired
    private ProductReviewService reviewService;

    /**
     * 分页查询商品评价
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "分页查询商品评价", description = "获取商品的评价列表（含用户信息）")
    public Result<IPage<ProductReview>> getProductReviews(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "评分筛选（1-5星）", example = "5")
            @RequestParam(required = false) Integer rating) {
        log.info("分页查询商品评价：productId={}, rating={}", productId, rating);

        Page<ProductReview> page = new Page<>(pageNum, pageSize);
        IPage<ProductReview> reviewPage = reviewService.getReviewPage(page, productId, rating);

        return Result.success(reviewPage);
    }

    /**
     * 获取用户评价列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户评价列表", description = "获取用户的所有评价记录")
    public Result<List<ProductReview>> getUserReviews(
            @Parameter(description = "用户ID", required = true, example = "1")
            @PathVariable Long userId) {
        log.info("获取用户评价列表：userId={}", userId);

        List<ProductReview> reviews = reviewService.getUserReviews(userId);

        return Result.success(reviews);
    }

    /**
     * 添加评价
     */
    @PostMapping
    @Operation(summary = "添加评价", description = "用户添加商品评价")
    public Result<Void> addReview(@RequestBody ProductReview review) {
        log.info("添加评价：userId={}, productId={}", review.getUserId(), review.getProductId());

        reviewService.addReview(review);

        return Result.success("评价添加成功");
    }

    /**
     * 商家回复评价
     */
    @PutMapping("/{reviewId}/reply")
    @Operation(summary = "商家回复评价", description = "商家回复用户评价")
    public Result<Void> replyReview(
            @Parameter(description = "评价ID", required = true, example = "1")
            @PathVariable Long reviewId,
            @Parameter(description = "回复内容", example = "感谢您的评价")
            @RequestParam String reply) {
        log.info("商家回复评价：reviewId={}", reviewId);

        reviewService.replyReview(reviewId, reply);

        return Result.success("回复成功");
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除评价", description = "用户删除自己的评价")
    public Result<Void> deleteReview(
            @Parameter(description = "评价ID", required = true, example = "1")
            @PathVariable Long reviewId,
            @Parameter(description = "用户ID", required = true, example = "1")
            @RequestParam Long userId) {
        log.info("删除评价：reviewId={}, userId={}", reviewId, userId);

        reviewService.deleteReview(reviewId, userId);

        return Result.success("评价删除成功");
    }

    /**
     * 获取商品评价统计
     */
    @GetMapping("/product/{productId}/statistics")
    @Operation(summary = "获取商品评价统计", description = "获取商品的评价统计数据（平均分、各星级数量）")
    public Result<Map<String, Object>> getReviewStatistics(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("获取商品评价统计：productId={}", productId);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("avgRating", reviewService.getAvgRating(productId));
        statistics.put("reviewCount", reviewService.getReviewCount(productId));
        statistics.put("ratingDistribution", reviewService.getRatingStatistics(productId));

        return Result.success(statistics);
    }
}

