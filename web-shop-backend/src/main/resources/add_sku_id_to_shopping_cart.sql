-- ======================================================
-- 添加 sku_id 列到购物车表
-- 日期: 2025-11-11
-- ======================================================

-- 检查列是否存在，如果不存在则添加
ALTER TABLE `shopping_cart` ADD COLUMN `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID' AFTER `product_id`;

-- 添加外键约束（如果 product_sku 表存在）
-- ALTER TABLE `shopping_cart` ADD CONSTRAINT `fk_cart_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`);
