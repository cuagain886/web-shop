package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.Product;

import java.util.List;

/**
 * 商品 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询商品（带分类名称）
     * 
     * @param page 分页对象
     * @param categoryId 分类ID（可为null）
     * @param keyword 关键词（可为null）
     * @param status 状态（可为null）
     * @return 商品分页数据
     */
    IPage<Product> selectPageWithCategory(Page<Product> page,
                                          @Param("categoryId") Long categoryId,
                                          @Param("keyword") String keyword,
                                          @Param("status") Integer status);

    /**
     * 查询热销商品
     * 
     * @param limit 数量限制
     * @return 热销商品列表
     */
    List<Product> selectHotProducts(@Param("limit") Integer limit);

    /**
     * 查询推荐商品
     * 
     * @param limit 数量限制
     * @return 推荐商品列表
     */
    List<Product> selectRecommendProducts(@Param("limit") Integer limit);

    /**
     * 查询秒杀商品
     *
     * @param limit 数量限制
     * @return 秒杀商品列表
     */
    List<Product> selectFlashSaleProducts(@Param("limit") Integer limit);

    /**
     * 更新商品销量
     * 
     * @param productId 商品ID
     * @param quantity 增加的销量
     */
    void updateSales(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 更新商品库存（增量更新）
     * 
     * @param productId 商品ID
     * @param quantity 库存变化量（正数增加，负数减少）
     */
    void updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 设置商品库存（绝对值）
     * 
     * @param productId 商品ID
     * @param stock 库存数量
     */
    void setStock(@Param("productId") Long productId, @Param("stock") Integer stock);

    /**
     * 批量更新商品状态
     * 
     * @param productIds 商品ID列表
     * @param status 状态
     */
    void batchUpdateStatus(@Param("productIds") List<Long> productIds, @Param("status") Integer status);
}

