package org.javaweb.webshopbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.javaweb.webshopbackend.mapper.ProductCategoryMapper;
import org.javaweb.webshopbackend.pojo.entity.ProductCategory;
import org.javaweb.webshopbackend.service.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类 Service 实现类
 * 
 * @author WebShop Team
 * @date 2025-11-03
 */
@Slf4j
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Override
    public List<ProductCategory> getRootCategories() {
        log.info("获取所有一级分类");

        return baseMapper.selectRootCategories();
    }

    @Override
    public List<ProductCategory> getChildCategories(Long parentId) {
        log.info("获取子分类：parentId={}", parentId);

        return baseMapper.selectByParentId(parentId);
    }

    @Override
    public List<ProductCategory> getCategoryTree() {
        log.info("获取分类树");

        // 1. 获取所有一级分类
        List<ProductCategory> rootCategories = getRootCategories();

        // 2. 为每个一级分类加载子分类
        // TODO: 在 ProductCategory 实体类中添加 children 字段（transient）后启用此功能
        // for (ProductCategory category : rootCategories) {
        //     List<ProductCategory> children = getChildCategories(category.getId());
        //     category.setChildren(children);
        // }

        return rootCategories;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(ProductCategory category) {
        log.info("添加分类：name={}, parentId={}", category.getName(), category.getParentId());

        // 设置默认值
        if (category.getSort() == null) {
            category.setSort(0);
        }

        this.save(category);

        log.info("分类添加成功：categoryId={}", category.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(ProductCategory category) {
        log.info("更新分类：categoryId={}", category.getId());

        this.updateById(category);

        log.info("分类更新成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        log.info("删除分类：categoryId={}", categoryId);

        // 检查是否可以删除
        if (!canDelete(categoryId)) {
            throw new IllegalArgumentException("该分类下有商品或子分类，无法删除");
        }

        this.removeById(categoryId);

        log.info("分类删除成功");
    }

    @Override
    public boolean canDelete(Long categoryId) {
        // 1. 检查是否有子分类
        Long childrenCount = baseMapper.countChildrenByCategoryId(categoryId);
        if (childrenCount > 0) {
            log.warn("分类下有{}个子分类，无法删除", childrenCount);
            return false;
        }

        // 2. 检查是否有商品
        Long productCount = baseMapper.countProductsByCategoryId(categoryId);
        if (productCount > 0) {
            log.warn("分类下有{}个商品，无法删除", productCount);
            return false;
        }

        return true;
    }

    @Override
    public List<ProductCategory> getCategoriesByLevel(Integer level) {
        log.info("根据层级获取分类列表：level={}", level);
        
        if (level == null) {
            return this.list();
        } else if (level == 1) {
            return getRootCategories();
        } else if (level == 2) {
            QueryWrapper<ProductCategory> wrapper = new QueryWrapper<>();
            wrapper.ne("parent_id", 0);
            return this.list(wrapper);
        }
        
        return this.list();
    }
}

