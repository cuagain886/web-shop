package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.ProductReviewMapper;
import org.javaweb.webshopbackend.pojo.entity.OrderItem;
import org.javaweb.webshopbackend.pojo.entity.ProductReview;
import org.javaweb.webshopbackend.service.OrderItemService;
import org.javaweb.webshopbackend.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评价 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class ProductReviewServiceImpl extends ServiceImpl<ProductReviewMapper, ProductReview> implements ProductReviewService {

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public IPage<ProductReview> getReviewPage(Page<ProductReview> page, Long productId, Integer rating) {
        log.info("分页查询商品评价：productId={}, rating={}", productId, rating);

        return baseMapper.selectPageWithUser(page, productId, rating);
    }

    @Override
    public List<ProductReview> getUserReviews(Long userId) {
        log.info("获取用户评价列表：userId={}", userId);

        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReview(ProductReview review) {
        log.info("添加评价：userId={}, productId={}, rating={}", 
                review.getUserId(), review.getProductId(), review.getRating());

        // 1. 验证订单项是否存在
        OrderItem orderItem = orderItemService.getById(review.getOrderItemId());
        if (orderItem == null) {
            throw new IllegalArgumentException("订单项不存在");
        }

        // 2. 检查是否已评价
        if (orderItem.getIsReviewed() == 1) {
            throw new IllegalArgumentException("该商品已评价");
        }

        // 3. 验证评分范围
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }

        // 4. 保存评价
        this.save(review);

        // 5. 更新订单项评价状态
        orderItem.setIsReviewed(1);
        orderItemService.updateById(orderItem);

        log.info("评价添加成功：reviewId={}", review.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long reviewId, String reply) {
        log.info("商家回复评价：reviewId={}", reviewId);

        ProductReview review = this.getById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }

        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        this.updateById(review);

        log.info("评价回复成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long reviewId, Long userId) {
        log.info("删除评价：reviewId={}, userId={}", reviewId, userId);

        // 验证权限
        ProductReview review = this.getById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权删除此评价");
        }

        // 逻辑删除
        this.removeById(reviewId);

        // 更新订单项评价状态
        OrderItem orderItem = orderItemService.getById(review.getOrderItemId());
        if (orderItem != null) {
            orderItem.setIsReviewed(0);
            orderItemService.updateById(orderItem);
        }

        log.info("评价删除成功");
    }

    @Override
    public Double getAvgRating(Long productId) {
        log.info("获取商品平均评分：productId={}", productId);

        Double avgRating = baseMapper.selectAvgRatingByProductId(productId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    public Long getReviewCount(Long productId) {
        log.info("获取商品评价数量：productId={}", productId);

        return baseMapper.countByProductId(productId);
    }

    @Override
    public Map<Integer, Long> getRatingStatistics(Long productId) {
        log.info("获取商品各星级评价统计：productId={}", productId);

        List<ProductReview> statistics = baseMapper.countByProductIdGroupByRating(productId);
        
        // 初始化所有星级为0
        Map<Integer, Long> result = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            result.put(i, 0L);
        }

        // 填充实际数据
        for (ProductReview review : statistics) {
            result.put(review.getRating(), (long) review.getId());
        }

        return result;
    }
}

