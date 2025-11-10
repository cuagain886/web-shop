package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.ProductSku;
import org.javaweb.webshopbackend.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品SKU Controller
 * 
 * @author WebShop Team
 * @date 2025-11-10
 */
@Slf4j
@RestController
@RequestMapping("/api/product-sku")
@Tag(name = "商品SKU管理", description = "商品SKU相关接口")
public class ProductSkuController {

    @Autowired
    private ProductSkuService productSkuService;

    /**
     * 根据商品ID获取SKU列表
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "获取商品SKU列表", description = "根据商品ID获取所有SKU")
    public Result<List<ProductSku>> getSkuList(
            @Parameter(description = "商品ID", required = true, example = "1")
            @PathVariable Long productId) {
        log.info("获取商品SKU列表：productId={}", productId);
        
        List<ProductSku> skuList = productSkuService.getByProductId(productId);
        
        return Result.success(skuList);
    }

    /**
     * 创建SKU
     */
    @PostMapping
    @Operation(summary = "创建SKU", description = "为商品创建新的SKU")
    public Result<Void> createSku(@RequestBody ProductSku sku) {
        log.info("创建SKU：productId={}, skuName={}", sku.getProductId(), sku.getSkuName());
        
        productSkuService.save(sku);
        
        return Result.success("SKU创建成功");
    }

    /**
     * 更新SKU
     */
    @PutMapping("/{skuId}")
    @Operation(summary = "更新SKU", description = "更新SKU信息")
    public Result<Void> updateSku(
            @Parameter(description = "SKU ID", required = true, example = "1")
            @PathVariable Long skuId,
            @RequestBody ProductSku sku) {
        log.info("更新SKU：skuId={}", skuId);
        
        sku.setId(skuId);
        productSkuService.updateById(sku);
        
        return Result.success("SKU更新成功");
    }

    /**
     * 删除SKU
     */
    @DeleteMapping("/{skuId}")
    @Operation(summary = "删除SKU", description = "删除指定SKU")
    public Result<Void> deleteSku(
            @Parameter(description = "SKU ID", required = true, example = "1")
            @PathVariable Long skuId) {
        log.info("删除SKU：skuId={}", skuId);
        
        productSkuService.removeById(skuId);
        
        return Result.success("SKU删除成功");
    }
}