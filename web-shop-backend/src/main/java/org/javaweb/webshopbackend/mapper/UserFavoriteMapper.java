package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.UserFavorite;

import java.util.List;

/**
 * 用户收藏 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 查询用户收藏列表（带商品信息）
     * 
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserFavorite> selectByUserIdWithProduct(@Param("userId") Long userId);

    /**
     * 检查用户是否收藏了某商品
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 收藏记录
     */
    UserFavorite selectByUserIdAndProductId(@Param("userId") Long userId,
                                           @Param("productId") Long productId);

    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void deleteByUserIdAndProductId(@Param("userId") Long userId,
                                    @Param("productId") Long productId);

    /**
     * 清空用户收藏
     * 
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 统计商品被收藏次数
     * 
     * @param productId 商品ID
     * @return 收藏次数
     */
    Long countByProductId(@Param("productId") Long productId);
}

