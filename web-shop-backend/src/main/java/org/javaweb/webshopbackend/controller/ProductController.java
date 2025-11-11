package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.dto.BatchProductDTO;
import org.javaweb.webshopbackend.pojo.dto.ProductQueryDTO;
import org.javaweb.webshopbackend.pojo.dto.UpdateStockDTO;
import org.javaweb.webshopbackend.pojo.entity.Product;
import org.javaweb.webshopbackend.pojo.entity.ProductSku;
import org.javaweb.webshopbackend.service.ProductService;
import org.javaweb.webshopbackend.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ProductSkuService productSkuService;

    @Autowired
    private ObjectMapper objectMapper;

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
    public Result<java.util.Map<String, Object>> getProductDetail(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("获取商品详情：productId={}", productId);

        Product product = productService.getProductDetail(productId);
        
        // 获取商品的SKU列表
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductSku> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ProductSku::getProductId, productId);
        wrapper.eq(ProductSku::getStatus, 1);
        List<ProductSku> skus = productSkuService.list(wrapper);
        
        // 构建返回数据
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.putAll(objectMapper.convertValue(product, new TypeReference<java.util.Map<String, Object>>() {}));
        result.put("skus", skus);

        return Result.success(result);
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
     * 获取秒杀商品
     */
    @GetMapping("/flash-sale")
    @Operation(summary = "获取秒杀商品", description = "获取秒杀商品列表")
    public Result<List<Product>> getFlashSaleProducts(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取秒杀商品：limit={}", limit);

        List<Product> flashSaleProducts = productService.getFlashSaleProducts(limit);

        return Result.success(flashSaleProducts);
    }

    /**
     * 管理端分页查询商品列表
     */
    @GetMapping("/admin/list")
    @Operation(summary = "管理端商品列表", description = "管理员查询商品列表，支持状态筛选")
    public Result<IPage<Product>> getAdminProductList(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类ID", example = "1")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "商品名称", example = "iPhone")
            @RequestParam(required = false) String name,
            @Parameter(description = "商品状态（0-下架，1-上架）", example = "1")
            @RequestParam(required = false) Integer status) {
        log.info("管理端查询商品列表：page={}, pageSize={}, categoryId={}, name={}, status={}",
                 page, pageSize, categoryId, name, status);

        Page<Product> pageObj = new Page<>(page, pageSize);
        IPage<Product> productPage = productService.getAdminProductPage(
                pageObj, categoryId, name, status
        );

        return Result.success(productPage);
    }

    /**
     * 获取商品详情（管理端）
     */
    @GetMapping("/admin/{productId}")
    @Operation(summary = "获取商品详情（管理端）", description = "管理员获取商品详细信息")
    public Result<Product> getAdminProductDetail(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("管理端获取商品详情：productId={}", productId);

        Product product = productService.getProductDetail(productId);

        return Result.success(product);
    }

    /**
     * 新增商品（管理员功能）
     */
    @PostMapping
    @Operation(summary = "新增商品", description = "管理员添加新商品")
    public Result<Product> addProduct(@Valid @RequestBody java.util.Map<String, Object> requestData) {
        try {
            // 解析商品信息
            Product product = objectMapper.convertValue(requestData, Product.class);
            log.info("新增商品：name={}", product.getName());

            productService.addProduct(product);

            // 处理SKU数据
            if (requestData.containsKey("skus") && requestData.get("skus") != null) {
                List<java.util.Map<String, Object>> skusData =
                    (List<java.util.Map<String, Object>>) requestData.get("skus");
                
                for (java.util.Map<String, Object> skuData : skusData) {
                    ProductSku sku = new ProductSku();
                    sku.setProductId(product.getId());
                    sku.setSkuCode((String) skuData.get("skuCode"));
                    sku.setSkuName((String) skuData.get("skuName"));
                    sku.setAttributes((String) skuData.get("attributes"));
                    sku.setPrice(new java.math.BigDecimal(skuData.get("price").toString()));
                    sku.setOriginalPrice(new java.math.BigDecimal(skuData.get("originalPrice").toString()));
                    sku.setStock(Integer.parseInt(skuData.get("stock").toString()));
                    sku.setStatus(1);
                    
                    productSkuService.save(sku);
                }
            }

            return Result.success("商品添加成功", product);
        } catch (Exception e) {
            log.error("新增商品失败", e);
            return Result.error("新增商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品（管理员功能）
     */
    @PutMapping("/{productId}")
    @Operation(summary = "更新商品", description = "管理员更新商品信息")
    public Result<Void> updateProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @RequestBody java.util.Map<String, Object> requestData) {
        try {
            log.info("更新商品：productId={}", productId);

            Product product = objectMapper.convertValue(requestData, Product.class);
            product.setId(productId);
            productService.updateProduct(product);

            // 处理SKU数据（先删除旧的，再添加新的）
            if (requestData.containsKey("skus") && requestData.get("skus") != null) {
                // 删除旧SKU
                com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductSku> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
                wrapper.eq(ProductSku::getProductId, productId);
                productSkuService.remove(wrapper);

                // 添加新SKU
                List<java.util.Map<String, Object>> skusData =
                    (List<java.util.Map<String, Object>>) requestData.get("skus");
                
                for (java.util.Map<String, Object> skuData : skusData) {
                    ProductSku sku = new ProductSku();
                    sku.setProductId(productId);
                    sku.setSkuCode((String) skuData.get("skuCode"));
                    sku.setSkuName((String) skuData.get("skuName"));
                    sku.setAttributes((String) skuData.get("attributes"));
                    sku.setPrice(new java.math.BigDecimal(skuData.get("price").toString()));
                    sku.setOriginalPrice(new java.math.BigDecimal(skuData.get("originalPrice").toString()));
                    sku.setStock(Integer.parseInt(skuData.get("stock").toString()));
                    sku.setStatus(1);
                    
                    productSkuService.save(sku);
                }
            }

            return Result.success("商品更新成功");
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.error("更新商品失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品（管理端）
     */
    @PutMapping("/admin/{productId}")
    @Operation(summary = "更新商品（管理端）", description = "管理员更新商品信息")
    public Result<Void> updateAdminProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @RequestBody Product product) {
        log.info("管理端更新商品：productId={}", productId);

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
     * 删除商品（管理端）
     */
    @DeleteMapping("/admin/{productId}")
    @Operation(summary = "删除商品（管理端）", description = "管理员删除商品（逻辑删除）")
    public Result<Void> deleteAdminProduct(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("管理端删除商品：productId={}", productId);

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
    public Result<Void> batchOnShelf(@Valid @RequestBody BatchProductDTO batchProductDTO) {
        log.info("批量上架商品：count={}", batchProductDTO.getProductIds().size());

        productService.batchUpdateProductStatus(batchProductDTO.getProductIds(), 1);

        return Result.success("批量上架成功");
    }

    /**
     * 批量下架商品
     */
    @PutMapping("/batch-off-shelf")
    @Operation(summary = "批量下架商品", description = "管理员批量下架商品")
    public Result<Void> batchOffShelf(@Valid @RequestBody BatchProductDTO batchProductDTO) {
        log.info("批量下架商品：count={}", batchProductDTO.getProductIds().size());

        productService.batchUpdateProductStatus(batchProductDTO.getProductIds(), 0);

        return Result.success("批量下架成功");
    }

    /**
     * 设置库存（绝对值）
     */
    @PutMapping("/{productId}/stock")
    @Operation(summary = "设置库存", description = "管理员设置商品库存为指定值")
    public Result<Void> updateStock(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockDTO updateStockDTO) {
        log.info("设置库存：productId={}, stock={}", productId, updateStockDTO.getStock());

        productService.setProductStock(productId, updateStockDTO.getStock());

        return Result.success("库存设置成功");
    }

    /**
     * 设置库存（管理端）
     */
    @PutMapping("/admin/{productId}/stock")
    @Operation(summary = "设置库存（管理端）", description = "管理员设置商品库存为指定值")
    public Result<Void> updateAdminStock(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId,
            @Valid @RequestBody UpdateStockDTO updateStockDTO) {
        log.info("管理端设置库存：productId={}, stock={}", productId, updateStockDTO.getStock());

        productService.setProductStock(productId, updateStockDTO.getStock());

        return Result.success("库存设置成功");
    }
}

