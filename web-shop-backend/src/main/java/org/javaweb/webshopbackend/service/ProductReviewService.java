package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.ProductReview;

import java.util.List;
import java.util.Map;

/**
 * 商品评价 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface ProductReviewService extends IService<ProductReview> {

    /**
     * 分页查询商品评价（带用户信息）
     * 
     * @param page 分页对象
     * @param productId 商品ID
     * @param rating 评分（可为null）
     * @return 评价分页数据
     */
    IPage<ProductReview> getReviewPage(Page<ProductReview> page, Long productId, Integer rating);

    /**
     * 获取用户的评价列表
     * 
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ProductReview> getUserReviews(Long userId);

    /**
     * 添加评价
     * 
     * @param review 评价信息
     */
    void addReview(ProductReview review);

    /**
     * 商家回复评价
     * 
     * @param reviewId 评价ID
     * @param reply 回复内容
     */
    void replyReview(Long reviewId, String reply);

    /**
     * 删除评价
     * 
     * @param reviewId 评价ID
     * @param userId 用户ID（验证权限）
     */
    void deleteReview(Long reviewId, Long userId);

    /**
     * 获取商品平均评分
     * 
     * @param productId 商品ID
     * @return 平均评分
     */
    Double getAvgRating(Long productId);

    /**
     * 获取商品评价数量
     * 
     * @param productId 商品ID
     * @return 评价数量
     */
    Long getReviewCount(Long productId);

    /**
     * 获取商品各星级评价统计
     * 
     * @param productId 商品ID
     * @return 各星级评价数量
     */
    Map<Integer, Long> getRatingStatistics(Long productId);
}

