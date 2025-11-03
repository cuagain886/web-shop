package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.UserFavoriteMapper;
import org.javaweb.webshopbackend.pojo.entity.UserFavorite;
import org.javaweb.webshopbackend.service.UserFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户收藏 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, Long productId) {
        log.info("添加收藏：userId={}, productId={}", userId, productId);

        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            log.warn("商品已在收藏列表中");
            throw new IllegalArgumentException("商品已在收藏列表中");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        this.save(favorite);

        log.info("收藏添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long productId) {
        log.info("取消收藏：userId={}, productId={}", userId, productId);

        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getProductId, productId);
        
        this.remove(wrapper);

        log.info("收藏取消成功");
    }

    @Override
    public List<UserFavorite> getFavoriteList(Long userId) {
        log.info("获取用户收藏列表：userId={}", userId);

        return baseMapper.selectByUserIdWithProduct(userId);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        log.info("检查是否已收藏：userId={}, productId={}", userId, productId);

        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getProductId, productId);
        
        return this.count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearFavorites(Long userId) {
        log.info("清空用户收藏：userId={}", userId);

        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId);
        
        this.remove(wrapper);

        log.info("收藏清空成功");
    }

    @Override
    public Long countByProduct(Long productId) {
        log.info("统计商品被收藏次数：productId={}", productId);

        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getProductId, productId);
        
        return this.count(wrapper);
    }
}

