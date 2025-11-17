package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.UserFavorite;

import java.util.List;

/**
 * 用户收藏 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface UserFavoriteService extends IService<UserFavorite> {

    /**
     * 添加收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void removeFavorite(Long userId, Long productId);

    /**
     * 获取用户收藏列表（带商品信息）
     * 
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserFavorite> getFavoriteList(Long userId);

    /**
     * 检查是否已收藏
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return true-已收藏，false-未收藏
     */
    boolean isFavorite(Long userId, Long productId);

    /**
     * 清空用户收藏
     * 
     * @param userId 用户ID
     */
    void clearFavorites(Long userId);

    /**
     * 统计商品被收藏次数
     * 
     * @param productId 商品ID
     * @return 收藏次数
     */
    Long countByProduct(Long productId);
}

