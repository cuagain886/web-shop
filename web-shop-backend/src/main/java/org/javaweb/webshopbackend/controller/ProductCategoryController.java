package org.javaweb.webshopbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.pojo.common.Result;
import org.javaweb.webshopbackend.pojo.entity.ProductCategory;
import org.javaweb.webshopbackend.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类Controller
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@RestController
@RequestMapping("/api/category")
@Tag(name = "商品分类管理", description = "商品分类相关接口")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    /**
     * 获取所有一级分类
     */
    @GetMapping("/root")
    @Operation(summary = "获取所有一级分类", description = "获取所有一级商品分类列表")
    public Result<List<ProductCategory>> getRootCategories() {
        log.info("获取所有一级分类");

        List<ProductCategory> categories = categoryService.getRootCategories();

        return Result.success(categories);
    }

    /**
     * 获取子分类
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "获取子分类", description = "根据父分类ID获取子分类列表")
    public Result<List<ProductCategory>> getChildCategories(
            @Parameter(description = "父分类ID", required = true, example = "1")
            @PathVariable Long parentId) {
        log.info("获取子分类：parentId={}", parentId);

        List<ProductCategory> children = categoryService.getChildCategories(parentId);

        return Result.success(children);
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取完整的商品分类树")
    public Result<List<ProductCategory>> getCategoryTree() {
        log.info("获取分类树");

        List<ProductCategory> tree = categoryService.getCategoryTree();

        return Result.success(tree);
    }

    /**
     * 获取分类列表（支持按层级筛选）
     */
    @GetMapping("/list")
    @Operation(summary = "获取分类列表", description = "获取分类列表，支持按层级筛选")
    public Result<List<ProductCategory>> getCategoryList(
            @Parameter(description = "层级（1-一级分类，2-二级分类）", example = "2")
            @RequestParam(required = false) Integer level) {
        log.info("获取分类列表：level={}", level);

        List<ProductCategory> categories = categoryService.getCategoriesByLevel(level);

        return Result.success(categories);
    }

    /**
     * 新增分类
     */
    @PostMapping
    @Operation(summary = "新增分类", description = "管理员添加新分类")
    public Result<Void> addCategory(@RequestBody ProductCategory category) {
        log.info("新增分类：name={}", category.getName());

        categoryService.addCategory(category);

        return Result.success("分类添加成功");
    }

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    @Operation(summary = "更新分类", description = "管理员更新分类信息")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @RequestBody ProductCategory category) {
        log.info("更新分类：categoryId={}", categoryId);

        category.setId(categoryId);
        categoryService.updateCategory(category);

        return Result.success("分类更新成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "删除分类", description = "管理员删除分类（需检查是否有商品或子分类）")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        log.info("删除分类：categoryId={}", categoryId);

        categoryService.deleteCategory(categoryId);

        return Result.success("分类删除成功");
    }
}

