package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.ProductSkuMapper;
import org.javaweb.webshopbackend.pojo.entity.ProductSku;
import org.javaweb.webshopbackend.service.ProductSkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品SKU Service实现类
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
@Slf4j
@Service
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {

    @Override
    public List<ProductSku> getByProductId(Long productId) {
        return baseMapper.selectByProductId(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(Long skuId, Integer quantity) {
        baseMapper.updateStock(skuId, quantity);
    }

    @Override
    public boolean checkStock(Long skuId, Integer quantity) {
        ProductSku sku = this.getById(skuId);
        return sku != null && sku.getStock() >= quantity;
    }
}