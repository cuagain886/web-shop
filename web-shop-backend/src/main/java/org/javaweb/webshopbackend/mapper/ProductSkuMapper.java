package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.javaweb.webshopbackend.pojo.entity.ProductSku;

import java.util.List;

/**
 * 商品SKU Mapper接口
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 根据商品ID查询SKU列表
     */
    List<ProductSku> selectByProductId(Long productId);

    /**
     * 更新SKU库存
     */
    int updateStock(Long skuId, Integer quantity);
}