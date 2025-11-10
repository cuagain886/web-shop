-- 为订单项表添加SKU ID字段
ALTER TABLE `order_item` 
ADD COLUMN `sku_id` BIGINT NULL COMMENT 'SKU ID' AFTER `product_id`;