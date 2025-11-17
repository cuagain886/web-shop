-- 创建商品SKU表
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `sku_code` VARCHAR(50) NOT NULL COMMENT 'SKU编码',
  `sku_name` VARCHAR(100) NOT NULL COMMENT '规格名称',
  `attributes` TEXT COMMENT '规格属性JSON',
  `price` DECIMAL(10, 2) NOT NULL COMMENT 'SKU价格',
  `original_price` DECIMAL(10, 2) COMMENT 'SKU原价',
  `stock` INT NOT NULL DEFAULT 0 COMMENT 'SKU库存',
  `sales` INT NOT NULL DEFAULT 0 COMMENT 'SKU销量',
  `image` VARCHAR(500) COMMENT 'SKU图片',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除,1-已删除)',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';