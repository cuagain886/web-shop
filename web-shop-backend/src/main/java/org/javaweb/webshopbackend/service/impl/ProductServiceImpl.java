package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.ProductMapper;
import org.javaweb.webshopbackend.pojo.entity.Product;
import org.javaweb.webshopbackend.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<Product> getProductPage(Page<Product> page, Long categoryId, String keyword,
                                         BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        log.info("分页查询商品：categoryId={}, keyword={}, sortBy={}", categoryId, keyword, sortBy);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)  // 只查上架商品
               .eq(categoryId != null, Product::getCategoryId, categoryId)
               .like(keyword != null && !keyword.isEmpty(), Product::getName, keyword)
               .ge(minPrice != null, Product::getPrice, minPrice)
               .le(maxPrice != null, Product::getPrice, maxPrice);

        // 排序
        if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSales);
        } else if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            // 默认综合排序：销量降序
            wrapper.orderByDesc(Product::getSales);
        }

        return this.page(page, wrapper);
    }

    @Override
    public IPage<Product> getAdminProductPage(Page<Product> page, Long categoryId, String keyword, Integer status) {
        log.info("管理端分页查询商品：categoryId={}, keyword={}, status={}", categoryId, keyword, status);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Product::getCategoryId, categoryId)
               .eq(status != null, Product::getStatus, status)
               .and(keyword != null && !keyword.isEmpty(), w -> w
                   .like(Product::getName, keyword)
                   .or()
                   .eq(Product::getId, keyword))
               .orderByDesc(Product::getCreatedTime);

        return this.page(page, wrapper);
    }

    @Override
    public Product getProductDetail(Long productId) {
        log.info("获取商品详情：productId={}", productId);

        Product product = this.getById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        return product;
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        log.info("获取热销商品：limit={}", limit);

        return baseMapper.selectHotProducts(limit);
    }

    @Override
    public List<Product> getRecommendProducts(Integer limit) {
        log.info("获取推荐商品：limit={}", limit);

        return baseMapper.selectRecommendProducts(limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product addProduct(Product product) {
        log.info("新增商品：name={}", product.getName());

        // 设置默认值
        if (product.getStatus() == null) {
            product.setStatus(0);  // 默认待上架
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getIsHot() == null) {
            product.setIsHot(0);
        }
        if (product.getIsRecommend() == null) {
            product.setIsRecommend(0);
        }

        this.save(product);

        log.info("商品新增成功：productId={}", product.getId());
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Product product) {
        log.info("更新商品：productId={}", product.getId());

        // 不允许修改销量
        product.setSales(null);

        this.updateById(product);

        log.info("商品更新成功：productId={}", product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long productId) {
        log.info("删除商品：productId={}", productId);

        this.removeById(productId);

        log.info("商品删除成功：productId={}", productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteProducts(List<Long> productIds) {
        log.info("批量删除商品：productIds={}", productIds);

        this.removeByIds(productIds);

        log.info("批量删除成功：数量={}", productIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long productId, Integer status) {
        log.info("更新商品状态：productId={}, status={}", productId, status);

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        this.updateById(product);

        log.info("商品状态更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateProductStatus(List<Long> productIds, Integer status) {
        log.info("批量更新商品状态：productIds={}, status={}", productIds, status);

        baseMapper.batchUpdateStatus(productIds, status);

        log.info("批量更新状态成功：数量={}", productIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStock(Long productId, Integer quantity) {
        log.info("更新商品库存（增量）：productId={}, quantity={}", productId, quantity);

        baseMapper.updateStock(productId, quantity);

        log.info("库存更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setProductStock(Long productId, Integer stock) {
        log.info("设置商品库存（绝对值）：productId={}, stock={}", productId, stock);

        baseMapper.setStock(productId, stock);

        log.info("库存设置成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductSales(Long productId, Integer quantity) {
        log.info("更新商品销量：productId={}, quantity={}", productId, quantity);

        baseMapper.updateSales(productId, quantity);

        log.info("销量更新成功");
    }

    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }
        return product.getStock() >= quantity;
    }
}

