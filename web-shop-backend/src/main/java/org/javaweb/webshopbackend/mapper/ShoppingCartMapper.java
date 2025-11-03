package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    /**
     * 查询用户购物车（带商品信息）
     * 
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<ShoppingCart> selectCartWithProduct(@Param("userId") Long userId);

    /**
     * 查询用户已选中的购物车项
     * 
     * @param userId 用户ID
     * @return 已选中的购物车列表
     */
    List<ShoppingCart> selectCheckedByUserId(@Param("userId") Long userId);

    /**
     * 更新购物车项选中状态
     * 
     * @param userId 用户ID
     * @param checked 是否选中
     */
    void updateCheckedByUserId(@Param("userId") Long userId, @Param("checked") Integer checked);

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户已选中的购物车项
     * 
     * @param userId 用户ID
     */
    void deleteCheckedByUserId(@Param("userId") Long userId);
}

