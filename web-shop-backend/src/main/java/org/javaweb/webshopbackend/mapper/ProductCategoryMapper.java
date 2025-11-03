package org.javaweb.webshopbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.javaweb.webshopbackend.pojo.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类 Mapper 接口
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * 查询所有一级分类
     * 
     * @return 一级分类列表
     */
    List<ProductCategory> selectRootCategories();

    /**
     * 查询指定父分类下的子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<ProductCategory> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 统计分类下的商品数量（用于删除前检查）
     * 
     * @param categoryId 分类ID
     * @return 商品数量
     */
    Long countProductsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 统计分类下的子分类数量（用于删除前检查）
     * 
     * @param categoryId 分类ID
     * @return 子分类数量
     */
    Long countChildrenByCategoryId(@Param("categoryId") Long categoryId);
}

