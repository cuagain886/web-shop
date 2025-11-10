package org.javaweb.webshopbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.javaweb.webshopbackend.pojo.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类 Service 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取所有一级分类
     * 
     * @return 一级分类列表
     */
    List<ProductCategory> getRootCategories();

    /**
     * 获取指定父分类下的子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<ProductCategory> getChildCategories(Long parentId);

    /**
     * 获取分类树（包含子分类）
     * 
     * @return 分类树
     */
    List<ProductCategory> getCategoryTree();

    /**
     * 添加分类
     * 
     * @param category 分类信息
     */
    void addCategory(ProductCategory category);

    /**
     * 更新分类
     * 
     * @param category 分类信息
     */
    void updateCategory(ProductCategory category);

    /**
     * 删除分类
     * 
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);

    /**
     * 检查分类是否可以删除
     * 
     * @param categoryId 分类ID
     * @return true-可以删除，false-不可删除
     */
    boolean canDelete(Long categoryId);

    /**
     * 根据层级获取分类列表
     *
     * @param level 层级（1-一级分类，2-二级分类，null-所有分类）
     * @return 分类列表
     */
    List<ProductCategory> getCategoriesByLevel(Integer level);
}

