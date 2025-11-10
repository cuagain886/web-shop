package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.ProductSku;

import java.util.List;

/**
 * 商品SKU Service接口
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
public interface ProductSkuService extends IService<ProductSku> {

    /**
     * 根据商品ID获取SKU列表
     */
    List<ProductSku> getByProductId(Long productId);

    /**
     * 更新SKU库存
     */
    void updateStock(Long skuId, Integer quantity);

    /**
     * 检查SKU库存
     */
    boolean checkStock(Long skuId, Integer quantity);
}