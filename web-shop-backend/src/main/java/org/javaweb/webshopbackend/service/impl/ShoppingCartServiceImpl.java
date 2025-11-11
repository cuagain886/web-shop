package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.ShoppingCartMapper;
import org.javaweb.webshopbackend.pojo.entity.Product;
import org.javaweb.webshopbackend.pojo.entity.ShoppingCart;
import org.javaweb.webshopbackend.service.ProductService;
import org.javaweb.webshopbackend.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(Long userId, Long productId, Integer quantity, String specInfo, Long skuId) {
        log.info("添加到购物车：userId={}, productId={}, quantity={}, skuId={}", userId, productId, quantity, skuId);

        // 1. 检查商品是否存在且上架
        Product product = productService.getById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        if (product.getStatus() == 0) {
            throw new IllegalArgumentException("商品已下架");
        }

        // 2. 检查库存
        if (!productService.checkStock(productId, quantity)) {
            throw new IllegalArgumentException("商品库存不足");
        }

        // 3. 查询购物车中是否已有相同商品（相同规格）
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
               .eq(ShoppingCart::getProductId, productId);
        if (specInfo != null) {
            wrapper.eq(ShoppingCart::getSpecInfo, specInfo);
        } else {
            wrapper.isNull(ShoppingCart::getSpecInfo);
        }
        ShoppingCart cartItem = this.getOne(wrapper);

        if (cartItem != null) {
            // 已存在，更新数量
            Integer newQuantity = cartItem.getQuantity() + quantity;
            // 再次检查库存
            if (!productService.checkStock(productId, newQuantity)) {
                throw new IllegalArgumentException("商品库存不足");
            }
            cartItem.setQuantity(newQuantity);
            this.updateById(cartItem);
            log.info("购物车商品数量已更新：cartId={}, quantity={}", cartItem.getId(), newQuantity);
        } else {
            // 新增
            cartItem = new ShoppingCart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setSkuId(skuId);
            cartItem.setQuantity(quantity);
            cartItem.setSpecInfo(specInfo);
            cartItem.setChecked(1);  // 默认选中
            this.save(cartItem);
            log.info("商品已添加到购物车：cartId={}", cartItem.getId());
        }
    }

    @Override
    public List<ShoppingCart> getCartList(Long userId) {
        log.info("获取购物车列表：userId={}", userId);

        // 使用自定义方法查询（带商品信息）
        return baseMapper.selectCartWithProduct(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long cartId, Integer quantity) {
        log.info("更新购物车数量：cartId={}, quantity={}", cartId, quantity);

        // 1. 查询购物车项
        ShoppingCart cartItem = this.getById(cartId);
        if (cartItem == null) {
            throw new IllegalArgumentException("购物车项不存在");
        }

        // 2. 检查库存
        if (!productService.checkStock(cartItem.getProductId(), quantity)) {
            throw new IllegalArgumentException("商品库存不足");
        }

        // 3. 更新数量
        cartItem.setQuantity(quantity);
        this.updateById(cartItem);

        log.info("购物车数量更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChecked(Long cartId, Integer checked) {
        log.info("更新购物车选中状态：cartId={}, checked={}", cartId, checked);

        ShoppingCart cartItem = new ShoppingCart();
        cartItem.setId(cartId);
        cartItem.setChecked(checked);
        this.updateById(cartItem);

        log.info("购物车选中状态更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAllChecked(Long userId, Integer checked) {
        log.info("更新用户所有购物车选中状态：userId={}, checked={}", userId, checked);

        baseMapper.updateCheckedByUserId(userId, checked);

        log.info("全部购物车选中状态更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCart(Long cartId) {
        log.info("删除购物车项：cartId={}", cartId);

        this.removeById(cartId);

        log.info("购物车项删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCarts(List<Long> cartIds) {
        log.info("批量删除购物车项：cartIds={}", cartIds);

        this.removeByIds(cartIds);

        log.info("批量删除成功：数量={}", cartIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        log.info("清空购物车：userId={}", userId);

        baseMapper.deleteByUserId(userId);

        log.info("购物车已清空");
    }

    @Override
    public List<ShoppingCart> getCheckedItems(Long userId) {
        log.info("获取已选中的购物车项：userId={}", userId);

        return baseMapper.selectCheckedByUserId(userId);
    }

    @Override
    public Integer countCartItems(Long userId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        
        List<ShoppingCart> cartItems = this.list(wrapper);
        int total = 0;
        for (ShoppingCart item : cartItems) {
            total += item.getQuantity();
        }
        
        return total;
    }
}

