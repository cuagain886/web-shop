-- ======================================================
-- WebShop 电商平台数据库初始化脚本
-- 优化版本 - 适配前端功能
-- 日期: 2025-11-17
-- ======================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS webshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webshop;

-- ======================================================
-- 1. 用户表（合并普通用户和管理员）
-- ======================================================
CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名（唯一）',
    `password` varchar(100) NOT NULL COMMENT '加密密码（BCrypt，实际长度60）',
    `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号（唯一）',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色（user-普通用户，merchant-商家）',
    `gender` tinyint DEFAULT NULL COMMENT '性别（0-女，1-男，2-保密）',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表（含普通用户和商家）';

-- ======================================================
-- 2. 用户地址表
-- ======================================================
CREATE TABLE `user_address` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` bigint NOT NULL COMMENT '关联用户ID',
    `receiver_name` varchar(50) NOT NULL COMMENT '收件人姓名',
    `receiver_phone` varchar(20) NOT NULL COMMENT '收件人电话',
    `province` varchar(50) NOT NULL COMMENT '省份',
    `city` varchar(50) NOT NULL COMMENT '城市',
    `district` varchar(50) NOT NULL COMMENT '区/县',
    `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
    `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址（0-否，1-是）',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_default` (`is_default`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- ======================================================
-- 3. 商品分类表
-- ======================================================
CREATE TABLE `product_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` varchar(100) NOT NULL COMMENT '分类名称',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID（0-一级分类）',
    `icon` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
    `sort` int NOT NULL DEFAULT 0 COMMENT '排序（值越小越靠前）',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort` (`sort`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表（支持二级分类）';

-- ======================================================
-- 4. 商品表
-- ======================================================
CREATE TABLE `product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` varchar(200) NOT NULL COMMENT '商品名称',
    `category_id` bigint NOT NULL COMMENT '关联分类ID',
    `price` decimal(10,2) NOT NULL COMMENT '商品单价（元）',
    `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价（元）',
    `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量',
    `sales` int NOT NULL DEFAULT 0 COMMENT '销售数量',
    `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
    `images` varchar(1000) DEFAULT NULL COMMENT '商品图片（JSON数组）',
    `description` text COMMENT '商品描述（富文本）',
    `specs` text COMMENT '商品规格（JSON格式，如：{"颜色":["红色","蓝色"],"尺寸":["S","M","L"]}）',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
    `is_hot` tinyint NOT NULL DEFAULT 0 COMMENT '是否热销（0-否，1-是）',
    `is_recommend` tinyint NOT NULL DEFAULT 0 COMMENT '是否推荐（0-否，1-是）',
    `is_flash_sale` tinyint NOT NULL DEFAULT 0 COMMENT '是否限时秒杀（0-否，1-是）',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_price` (`price`),
    KEY `idx_sales` (`sales`),
    KEY `idx_name` (`name`),
    KEY `idx_is_hot` (`is_hot`),
    KEY `idx_is_recommend` (`is_recommend`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_is_flash_sale` (`is_flash_sale`),
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- ======================================================
-- 5. 商品SKU表
-- ======================================================
CREATE TABLE `product_sku` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    `product_id` bigint NOT NULL COMMENT '商品ID',
    `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
    `sku_name` varchar(100) NOT NULL COMMENT '规格名称',
    `attributes` text COMMENT '规格属性JSON',
    `price` decimal(10,2) NOT NULL COMMENT 'SKU价格',
    `original_price` decimal(10,2) DEFAULT NULL COMMENT 'SKU原价',
    `stock` int NOT NULL DEFAULT 0 COMMENT 'SKU库存',
    `sales` int NOT NULL DEFAULT 0 COMMENT 'SKU销量',
    `image` varchar(500) DEFAULT NULL COMMENT 'SKU图片',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(0-禁用,1-启用)',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除,1-已删除)',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品SKU表';

-- ======================================================
-- 6. 购物车表
-- ======================================================
CREATE TABLE `shopping_cart` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id` bigint NOT NULL COMMENT '关联用户ID',
    `product_id` bigint NOT NULL COMMENT '关联商品ID',
    `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
    `spec_info` varchar(200) DEFAULT NULL COMMENT '规格信息（如："颜色:红色,尺寸:XL"）',
    `quantity` int NOT NULL DEFAULT 1 COMMENT '商品数量',
    `checked` tinyint NOT NULL DEFAULT 1 COMMENT '是否选中（0-否，1-是）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_checked` (`checked`),
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ======================================================
-- 7. 订单表（改名避免关键字冲突）
-- ======================================================
CREATE TABLE `orders` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` varchar(50) NOT NULL COMMENT '订单编号（唯一）',
    `user_id` bigint NOT NULL COMMENT '关联用户ID',
    `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额（元）',
    `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额（元）',
    `freight` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费（元）',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-退款中，6-已退款）',
    `payment_method` tinyint DEFAULT NULL COMMENT '支付方式（1-微信，2-支付宝）',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
    `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
    `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` varchar(200) DEFAULT NULL COMMENT '取消原因',
    `receiver_name` varchar(50) NOT NULL COMMENT '收件人姓名',
    `receiver_phone` varchar(20) NOT NULL COMMENT '收件人电话',
    `receiver_address` varchar(255) NOT NULL COMMENT '收件人完整地址',
    `express_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
    `tracking_no` varchar(100) DEFAULT NULL COMMENT '物流单号',
    `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_pay_time` (`pay_time`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ======================================================
-- 8. 订单项表
-- ======================================================
CREATE TABLE `order_item` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
    `order_id` bigint NOT NULL COMMENT '关联订单ID',
    `product_id` bigint NOT NULL COMMENT '关联商品ID',
    `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
    `product_name` varchar(200) NOT NULL COMMENT '商品名称（冗余）',
    `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片（冗余）',
    `spec_info` varchar(200) DEFAULT NULL COMMENT '规格信息（如："颜色:红色,尺寸:XL"）',
    `unit_price` decimal(10,2) NOT NULL COMMENT '购买时单价（元）',
    `quantity` int NOT NULL COMMENT '购买数量',
    `total_price` decimal(10,2) NOT NULL COMMENT '小计金额（元）',
    `is_reviewed` tinyint NOT NULL DEFAULT 0 COMMENT '是否已评价（0-未评价，1-已评价）',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_is_reviewed` (`is_reviewed`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ======================================================
-- 9. 商品评价表
-- ======================================================
CREATE TABLE `product_review` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_item_id` bigint NOT NULL COMMENT '关联订单项ID',
    `user_id` bigint NOT NULL COMMENT '关联用户ID',
    `product_id` bigint NOT NULL COMMENT '关联商品ID',
    `rating` tinyint NOT NULL COMMENT '评分（1-5星）',
    `content` text COMMENT '评价内容',
    `images` varchar(1000) DEFAULT NULL COMMENT '评价图片（JSON数组）',
    `reply` varchar(500) DEFAULT NULL COMMENT '商家回复',
    `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_item` (`order_item_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_rating` (`rating`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_review_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_item` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_review_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
    CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- ======================================================
-- 10. 退款表
-- ======================================================
CREATE TABLE `refund` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    `order_id` bigint NOT NULL COMMENT '关联订单ID',
    `refund_no` varchar(50) NOT NULL COMMENT '退款单号（唯一）',
    `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额（元）',
    `reason` varchar(500) NOT NULL COMMENT '退款原因',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-申请中，1-已同意，2-已拒绝）',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_refund_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款表';

-- ======================================================
-- 11. 用户收藏表
-- ======================================================
CREATE TABLE `user_favorite` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` bigint NOT NULL COMMENT '关联用户ID',
    `product_id` bigint NOT NULL COMMENT '关联商品ID',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_favorite_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- ======================================================
-- 12. 浏览历史表
-- ======================================================
CREATE TABLE `browsing_history` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
    `user_id` bigint NOT NULL COMMENT '关联用户ID',
    `product_id` bigint NOT NULL COMMENT '关联商品ID',
    `view_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_time` (`user_id`, `view_time`),
    CONSTRAINT `fk_history_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览历史表';

-- ======================================================
-- 13. 操作日志表
-- ======================================================
CREATE TABLE `operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `operator_id` bigint NOT NULL COMMENT '操作人ID',
    `operator_name` varchar(50) NOT NULL COMMENT '操作人名称',
    `operation_type` varchar(50) NOT NULL COMMENT '操作类型（如：商品管理、订单管理）',
    `operation_content` varchar(500) NOT NULL COMMENT '操作内容',
    `operation_object` varchar(100) DEFAULT NULL COMMENT '操作对象（如：商品ID、订单号）',
    `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_log_operator` FOREIGN KEY (`operator_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ======================================================
-- 14. 系统设置表
-- ======================================================
CREATE TABLE `system_settings` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
    `site_name` varchar(100) DEFAULT '购物平台' COMMENT '网站名称',
    `site_description` varchar(500) DEFAULT NULL COMMENT '网站描述',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
    `order_cancel_time` int DEFAULT 24 COMMENT '自动取消订单时间（小时）',
    `order_confirm_time` int DEFAULT 7 COMMENT '自动确认收货时间（天）',
    `stock_warning` int DEFAULT 10 COMMENT '库存预警数量',
    `default_shipping` decimal(10,2) DEFAULT 10.00 COMMENT '默认运费',
    `allow_register` tinyint(1) DEFAULT 1 COMMENT '是否允许用户注册',
    `allow_comment` tinyint(1) DEFAULT 1 COMMENT '是否允许商品评价',
    `maintenance_mode` tinyint(1) DEFAULT 0 COMMENT '是否开启维护模式',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- ======================================================
-- 15. 公告表
-- ======================================================
CREATE TABLE `announcement` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` varchar(255) NOT NULL COMMENT '公告标题',
    `content` longtext NOT NULL COMMENT '公告内容',
    `type` int DEFAULT 1 COMMENT '公告类型（1-普通公告，2-重要公告，3-活动公告）',
    `is_top` int DEFAULT 0 COMMENT '是否置顶（0-否，1-是）',
    `status` int DEFAULT 0 COMMENT '状态（0-草稿，1-发布）',
    `deleted` int DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ======================================================
-- 初始化数据
-- ======================================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 插入管理员用户（用户名: admin, 密码: 123456）
-- 密码使用BCrypt加密：$2a$10$4Ts1nEuLKq90Zx8s5bzQLuUXTI6SHcVpBgGKdYjk3Hpoogws5ULIu
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$4Ts1nEuLKq90Zx8s5bzQLuUXTI6SHcVpBgGKdYjk3Hpoogws5ULIu', '系统管理员', 'merchant', 1);

-- 插入普通用户（用户名: user, 密码: 123456）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('user', '$2a$10$4Ts1nEuLKq90Zx8s5bzQLuUXTI6SHcVpBgGKdYjk3Hpoogws5ULIu', '普通用户', 'user', 1);

-- 插入测试用户地址数据
-- 管理员用户（user_id=1）的地址
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) VALUES
(1, '张三', '13800138000', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001室', 1),
(1, '张三', '13800138000', '北京市', '北京市', '海淀区', '中关村大街1号海龙大厦5层', 0);

-- 普通用户（user_id=2）的地址
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`) VALUES
(2, '李四', '13900139000', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号恒生银行大厦2202室', 1),
(2, '李四', '13900139000', '上海市', '上海市', '徐汇区', '漕溪北路88号圣爱大厦东楼9层', 0),
(2, '王五', '13700137000', '广东省', '深圳市', '南山区', '科技园南区深圳湾科技生态园10栋A座', 0);

-- 插入商品分类数据
INSERT INTO `product_category` (`id`, `name`, `parent_id`, `icon`, `sort`, `deleted`, `created_time`, `updated_time`) VALUES
(1, '家用电器', 0, '📺', 1, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(2, '手机数码', 0, '📱', 2, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(3, '电脑办公', 0, '💻', 3, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(4, '家居家具', 0, '🛋️', 4, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(5, '服装鞋靴', 0, '👕', 5, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(6, '美妆个护', 0, '💄', 6, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(7, '运动户外', 0, '⚽', 7, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(8, '食品生鲜', 0, '🍎', 8, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(9, '母婴玩具', 0, '🍼', 9, 0, '2025-11-09 21:48:40', '2025-11-09 21:48:40'),
(10, '图书文娱', 0, '📚', 10, 0, '2025-11-09 21:48:40', '2025-11-11 23:34:54');

-- 插入系统设置
INSERT INTO `system_settings` (`site_name`, `site_description`, `contact_phone`, `contact_email`, `order_cancel_time`, `order_confirm_time`, `stock_warning`, `default_shipping`) VALUES
('WebShop购物平台', '专业的在线购物平台，为您提供优质的商品和服务', '400-123-4567', 'support@webshop.com', 24, 7, 10, 10.00);

-- 插入公告数据
INSERT INTO `announcement` (`title`, `content`, `type`, `is_top`, `status`) VALUES
('欢迎来到WebShop', '欢迎使用WebShop在线购物平台，我们为您提供优质的商品和服务。', 1, 1, 1),
('系统维护通知', '系统将于本周日凌晨2点进行维护，预计维护时间为2小时，期间无法访问。', 2, 0, 1),
('新品上市', '本周新上市多款热销商品，欢迎浏览选购。', 1, 0, 1);