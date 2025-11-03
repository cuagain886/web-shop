package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品（用户端）
     * 
     * @param page 分页对象
     * @param categoryId 分类ID（可为null）
     * @param keyword 关键词（可为null）
     * @param minPrice 最低价格（可为null）
     * @param maxPrice 最高价格（可为null）
     * @param sortBy 排序方式（sales-销量，price-价格）
     * @return 商品分页数据
     */
    IPage<Product> getProductPage(Page<Product> page, Long categoryId, String keyword,
                                   BigDecimal minPrice, BigDecimal maxPrice, String sortBy);

    /**
     * 分页查询商品（管理端）
     * 
     * @param page 分页对象
     * @param categoryId 分类ID（可为null）
     * @param keyword 关键词（可为null）
     * @param status 状态（可为null）
     * @return 商品分页数据
     */
    IPage<Product> getAdminProductPage(Page<Product> page, Long categoryId, String keyword, Integer status);

    /**
     * 获取商品详情
     * 
     * @param productId 商品ID
     * @return 商品信息
     */
    Product getProductDetail(Long productId);

    /**
     * 获取热销商品
     * 
     * @param limit 数量限制
     * @return 热销商品列表
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 获取推荐商品
     * 
     * @param limit 数量限制
     * @return 推荐商品列表
     */
    List<Product> getRecommendProducts(Integer limit);

    /**
     * 新增商品
     * 
     * @param product 商品信息
     * @return 新增的商品
     */
    Product addProduct(Product product);

    /**
     * 更新商品
     * 
     * @param product 商品信息
     */
    void updateProduct(Product product);

    /**
     * 删除商品（逻辑删除）
     * 
     * @param productId 商品ID
     */
    void deleteProduct(Long productId);

    /**
     * 批量删除商品
     * 
     * @param productIds 商品ID列表
     */
    void batchDeleteProducts(List<Long> productIds);

    /**
     * 更新商品状态
     * 
     * @param productId 商品ID
     * @param status 状态（0-下架，1-上架）
     */
    void updateProductStatus(Long productId, Integer status);

    /**
     * 批量更新商品状态
     * 
     * @param productIds 商品ID列表
     * @param status 状态
     */
    void batchUpdateProductStatus(List<Long> productIds, Integer status);

    /**
     * 更新商品库存
     * 
     * @param productId 商品ID
     * @param quantity 库存变化量（正数增加，负数减少）
     */
    void updateProductStock(Long productId, Integer quantity);

    /**
     * 更新商品销量
     * 
     * @param productId 商品ID
     * @param quantity 销量增加量
     */
    void updateProductSales(Long productId, Integer quantity);

    /**
     * 检查商品库存是否充足
     * 
     * @param productId 商品ID
     * @param quantity 需要的数量
     * @return true-库存充足，false-库存不足
     */
    boolean checkStock(Long productId, Integer quantity);
}

