package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加到购物车
     *
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @param specInfo 规格信息
     * @param skuId SKU ID
     */
    void addToCart(Long userId, Long productId, Integer quantity, String specInfo, Long skuId);

    /**
     * 获取购物车列表（带商品信息）
     * 
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<ShoppingCart> getCartList(Long userId);

    /**
     * 更新购物车项数量
     * 
     * @param cartId 购物车项ID
     * @param quantity 新数量
     */
    void updateQuantity(Long cartId, Integer quantity);

    /**
     * 更新购物车项选中状态
     * 
     * @param cartId 购物车项ID
     * @param checked 是否选中（0-否，1-是）
     */
    void updateChecked(Long cartId, Integer checked);

    /**
     * 更新用户所有购物车项选中状态（全选/取消全选）
     * 
     * @param userId 用户ID
     * @param checked 是否选中
     */
    void updateAllChecked(Long userId, Integer checked);

    /**
     * 删除购物车项
     * 
     * @param cartId 购物车项ID
     */
    void removeCart(Long cartId);

    /**
     * 批量删除购物车项
     * 
     * @param cartIds 购物车项ID列表
     */
    void removeCarts(List<Long> cartIds);

    /**
     * 清空用户购物车
     * 
     * @param userId 用户ID
     */
    void clearCart(Long userId);

    /**
     * 获取用户已选中的购物车项
     * 
     * @param userId 用户ID
     * @return 已选中的购物车列表
     */
    List<ShoppingCart> getCheckedItems(Long userId);

    /**
     * 统计用户购物车商品数量
     * 
     * @param userId 用户ID
     * @return 商品总数量
     */
    Integer countCartItems(Long userId);
}

