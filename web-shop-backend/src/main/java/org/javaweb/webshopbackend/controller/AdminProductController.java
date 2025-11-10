package org.javaweb.webshopbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.dto.UpdateStockDTO;
import org.javaweb.webshopbackend.pojo.entity.Product;
import org.javaweb.webshopbackend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "管理端商品管理", description = "管理端商品相关接口")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    @Operation(summary = "管理端商品列表", description = "管理员查询商品列表")
    public Result<IPage<Product>> getAdminProductList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "商品名称") @RequestParam(required = false) String name,
            @Parameter(description = "商品状态") @RequestParam(required = false) Integer status) {
        log.info("管理端查询商品列表：page={}, pageSize={}, categoryId={}, name={}, status={}",
                 page, pageSize, categoryId, name, status);

        Page<Product> pageObj = new Page<>(page, pageSize);
        IPage<Product> productPage = productService.getAdminProductPage(pageObj, categoryId, name, status);

        return Result.success(productPage);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "获取商品详情", description = "管理员获取商品详细信息")
    public Result<Product> getProductDetail(@PathVariable Long productId) {
        log.info("管理端获取商品详情：productId={}", productId);
        Product product = productService.getProductDetail(productId);
        return Result.success(product);
    }

    @PostMapping
    @Operation(summary = "新增商品", description = "管理员添加新商品")
    public Result<Product> addProduct(@Valid @RequestBody Product product) {
        log.info("新增商品：name={}", product.getName());
        productService.addProduct(product);
        return Result.success("商品添加成功", product);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "更新商品", description = "管理员更新商品信息")
    public Result<Void> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        log.info("管理端更新商品：productId={}", productId);
        product.setId(productId);
        productService.updateProduct(product);
        return Result.success("商品更新成功");
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "删除商品", description = "管理员删除商品")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        log.info("管理端删除商品：productId={}", productId);
        productService.deleteProduct(productId);
        return Result.success("商品删除成功");
    }

    @PutMapping("/{productId}/stock")
    @Operation(summary = "设置库存", description = "管理员设置商品库存")
    public Result<Void> updateStock(@PathVariable Long productId, @Valid @RequestBody UpdateStockDTO updateStockDTO) {
        log.info("管理端设置库存：productId={}, stock={}", productId, updateStockDTO.getStock());
        productService.setProductStock(productId, updateStockDTO.getStock());
        return Result.success("库存设置成功");
    }

    @PutMapping("/{productId}/recommend")
    @Operation(summary = "更新推荐状态", description = "管理员更新商品推荐状态")
    public Result<Void> updateRecommend(@PathVariable Long productId, @RequestParam Integer isRecommend) {
        log.info("管理端更新推荐状态：productId={}, isRecommend={}", productId, isRecommend);
        productService.updateRecommendStatus(productId, isRecommend);
        return Result.success("推荐状态更新成功");
    }

    @PutMapping("/{productId}/flash-sale")
    @Operation(summary = "更新秒杀状态", description = "管理员更新商品秒杀状态")
    public Result<Void> updateFlashSale(@PathVariable Long productId, @RequestParam Integer isFlashSale) {
        log.info("管理端更新秒杀状态：productId={}, isFlashSale={}", productId, isFlashSale);
        productService.updateFlashSaleStatus(productId, isFlashSale);
        return Result.success("秒杀状态更新成功");
    }

    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除商品", description = "管理员批量删除商品")
    public Result<Void> batchDeleteProducts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        log.info("管理端批量删除商品：count={}", ids.size());
        productService.batchDeleteProducts(ids);
        return Result.success("批量删除成功");
    }
}