-- 添加限时秒杀字段
ALTER TABLE `product` ADD COLUMN `is_flash_sale` tinyint NOT NULL DEFAULT 0 COMMENT '是否限时秒杀（0-否，1-是）' AFTER `is_recommend`;

-- 添加索引
ALTER TABLE `product` ADD KEY `idx_is_flash_sale` (`is_flash_sale`);