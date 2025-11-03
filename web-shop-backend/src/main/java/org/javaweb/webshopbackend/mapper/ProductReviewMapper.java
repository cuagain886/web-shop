package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.ProductReview;

import java.util.List;

/**
 * 商品评价 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {

    /**
     * 分页查询商品评价（带用户信息）
     * 
     * @param page 分页对象
     * @param productId 商品ID
     * @param rating 评分（可为null，查询所有评分）
     * @return 评价分页数据
     */
    IPage<ProductReview> selectPageWithUser(Page<ProductReview> page,
                                            @Param("productId") Long productId,
                                            @Param("rating") Integer rating);

    /**
     * 查询用户的评价列表
     * 
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ProductReview> selectByUserId(@Param("userId") Long userId);

    /**
     * 统计商品的平均评分
     * 
     * @param productId 商品ID
     * @return 平均评分
     */
    Double selectAvgRatingByProductId(@Param("productId") Long productId);

    /**
     * 统计商品的评价数量
     * 
     * @param productId 商品ID
     * @return 评价数量
     */
    Long countByProductId(@Param("productId") Long productId);

    /**
     * 统计商品各星级评价数量
     * 
     * @param productId 商品ID
     * @return 各星级评价数量
     */
    List<ProductReview> countByProductIdGroupByRating(@Param("productId") Long productId);
}

