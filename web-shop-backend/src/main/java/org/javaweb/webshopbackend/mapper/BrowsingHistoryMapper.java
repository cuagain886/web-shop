package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.BrowsingHistory;

import java.util.List;

/**
 * 浏览历史 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface BrowsingHistoryMapper extends BaseMapper<BrowsingHistory> {

    /**
     * 查询用户浏览历史（带商品信息，按时间倒序）
     * 
     * @param userId 用户ID
     * @param limit 数量限制（可为null）
     * @return 浏览历史列表
     */
    List<BrowsingHistory> selectByUserIdWithProduct(@Param("userId") Long userId,
                                                    @Param("limit") Integer limit);

    /**
     * 查询用户是否已浏览某商品
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 浏览记录
     */
    BrowsingHistory selectByUserIdAndProductId(@Param("userId") Long userId,
                                              @Param("productId") Long productId);

    /**
     * 删除用户指定商品的浏览记录
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void deleteByUserIdAndProductId(@Param("userId") Long userId,
                                    @Param("productId") Long productId);

    /**
     * 清空用户浏览历史
     * 
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户超过指定天数的浏览记录
     * 
     * @param userId 用户ID
     * @param days 天数
     */
    void deleteOldRecords(@Param("userId") Long userId, @Param("days") Integer days);

    /**
     * 查询热门商品（基于浏览次数）
     * 
     * @param limit 数量限制
     * @return 商品ID列表
     */
    List<Long> selectHotProducts(@Param("limit") Integer limit);
}

