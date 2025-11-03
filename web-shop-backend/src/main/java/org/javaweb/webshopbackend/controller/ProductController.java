package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.dto.ProductQueryDTO;
import org.javaweb.webshopbackend.pojo.entity.Product;
import org.javaweb.webshopbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
@Tag(name = "商品管理", description = "商品相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询商品列表", description = "支持按分类、价格区间、关键词搜索")
    public Result<IPage<Product>> getProductList(@Valid ProductQueryDTO queryDTO) {
        log.info("分页查询商品列表：{}", queryDTO);

        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 根据status参数决定调用哪个方法
        IPage<Product> productPage;
        if (queryDTO.getStatus() != null) {
            // 管理端查询（包含状态筛选）
            productPage = productService.getAdminProductPage(
                    page,
                    queryDTO.getCategoryId(),
                    queryDTO.getName(),
                    queryDTO.getStatus()
            );
        } else {
            // 用户端查询（按销量排序）
            productPage = productService.getProductPage(
                    page,
                    queryDTO.getCategoryId(),
                    queryDTO.getName(),
                    queryDTO.getMinPrice(),
                    queryDTO.getMaxPrice(),
                    "sales"
            );
        }

        return Result.success(productPage);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    public Result<Product> getProductDetail(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("获取商品详情：productId={}", productId);

        Product product = productService.getProductDetail(productId);

        return Result.success(product);
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门商品", description = "获取热门商品列表")
    public Result<List<Product>> getHotProducts(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门商品：limit={}", limit);

        List<Product> hotProducts = productService.getHotProducts(limit);

        return Result.success(hotProducts);
    }

    /**
     * 获取推荐商品
     */
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐商品", description = "获取推荐商品列表")
    public Result<List<Product>> getRecommendProducts(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取推荐商品：limit={}", limit);

        List<Product> recommendProducts = productService.getRecommendProducts(limit);

        return Result.success(recommendProducts);
    }

    /**
     * 新增商品（管理员功能）
     */
    @PostMapping
    @Operation(summary = "新增商品", description = "管理员添加新商品")
    public Result<Product> addProduct(@Valid @RequestBody Product product) {
        log.info("新增商品：name={}", product.getName());

        productService.addProduct(product);

        return Result.success("商品添加成功", product);
    }

    /**
     * 更新商品（管理员功能）
     */
    @PutMapping("/{productId}")
    @Operation(summary = "更新商品", description = "管理员更新商品信息")
    public Result<Void> updateProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @RequestBody Product product) {
        log.info("更新商品：productId={}", productId);

        product.setId(productId);
        productService.updateProduct(product);

        return Result.success("商品更新成功");
    }

    /**
     * 删除商品（管理员功能）
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "删除商品", description = "管理员删除商品（逻辑删除）")
    public Result<Void> deleteProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("删除商品：productId={}", productId);

        productService.deleteProduct(productId);

        return Result.success("商品删除成功");
    }

    /**
     * 上架商品
     */
    @PutMapping("/{productId}/on-shelf")
    @Operation(summary = "上架商品", description = "管理员上架商品")
    public Result<Void> onShelfProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("上架商品：productId={}", productId);

        productService.updateProductStatus(productId, 1);

        return Result.success("商品已上架");
    }

    /**
     * 下架商品
     */
    @PutMapping("/{productId}/off-shelf")
    @Operation(summary = "下架商品", description = "管理员下架商品")
    public Result<Void> offShelfProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("下架商品：productId={}", productId);

        productService.updateProductStatus(productId, 0);

        return Result.success("商品已下架");
    }

    /**
     * 批量上架商品
     */
    @PutMapping("/batch-on-shelf")
    @Operation(summary = "批量上架商品", description = "管理员批量上架商品")
    public Result<Void> batchOnShelf(@RequestBody List<Long> productIds) {
        log.info("批量上架商品：count={}", productIds.size());

        productService.batchUpdateProductStatus(productIds, 1);

        return Result.success("批量上架成功");
    }

    /**
     * 批量下架商品
     */
    @PutMapping("/batch-off-shelf")
    @Operation(summary = "批量下架商品", description = "管理员批量下架商品")
    public Result<Void> batchOffShelf(@RequestBody List<Long> productIds) {
        log.info("批量下架商品：count={}", productIds.size());

        productService.batchUpdateProductStatus(productIds, 0);

        return Result.success("批量下架成功");
    }

    /**
     * 更新库存
     */
    @PutMapping("/{productId}/stock")
    @Operation(summary = "更新库存", description = "管理员更新商品库存")
    public Result<Void> updateStock(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @Parameter(description = "库存数量", required = true, example = "100")
            @RequestParam Integer stock) {
        log.info("更新库存：productId={}, stock={}", productId, stock);

        productService.updateProductStock(productId, stock);

        return Result.success("库存更新成功");
    }
}

