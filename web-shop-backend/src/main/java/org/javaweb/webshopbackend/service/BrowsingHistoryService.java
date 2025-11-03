package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.BrowsingHistory;

import java.util.List;

/**
 * 浏览历史 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface BrowsingHistoryService extends IService<BrowsingHistory> {

    /**
     * 记录浏览历史
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void recordBrowsing(Long userId, Long productId);

    /**
     * 获取用户浏览历史（带商品信息）
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 浏览历史列表
     */
    List<BrowsingHistory> getUserHistory(Long userId, Integer limit);

    /**
     * 删除指定浏览记录
     * 
     * @param userId 用户ID
     * @param historyId 历史记录ID
     */
    void deleteHistory(Long userId, Long historyId);

    /**
     * 清空用户浏览历史
     * 
     * @param userId 用户ID
     */
    void clearHistory(Long userId);

    /**
     * 获取热门商品（基于浏览次数）
     * 
     * @param limit 限制数量
     * @return 商品ID列表
     */
    List<Long> getHotProducts(Integer limit);
}

