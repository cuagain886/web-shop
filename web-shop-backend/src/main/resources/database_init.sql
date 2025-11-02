-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS web_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE web_shop;

-- 1. 用户表
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `username` varchar(50) NOT NULL COMMENT '用户名（唯一）',
                        `password` varchar(100) NOT NULL COMMENT '加密密码（BCrypt）',
                        `nickname` varchar(50) DEFAULT NULL COMMENT '用户昵称',
                        `phone` varchar(20) DEFAULT NULL COMMENT '手机号（唯一）',
                        `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
                        `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
                        `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`),
                        UNIQUE KEY `uk_phone` (`phone`),
                        KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 用户地址表
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
                                `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_user_id` (`user_id`),
                                KEY `idx_is_default` (`is_default`),
                                CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 3. 管理员表
CREATE TABLE `admin` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
                         `username` varchar(50) NOT NULL COMMENT '管理员用户名（唯一）',
                         `password` varchar(100) NOT NULL COMMENT '加密密码（BCrypt）',
                         `nickname` varchar(50) DEFAULT NULL COMMENT '管理员昵称',
                         `phone` varchar(20) DEFAULT NULL COMMENT '联系电话（唯一）',
                         `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
                         `role` tinyint NOT NULL DEFAULT 1 COMMENT '角色（0-超级管理员，1-普通管理员）',
                         `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
                         `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                         `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_admin_username` (`username`),
                         UNIQUE KEY `uk_admin_phone` (`phone`),
                         KEY `idx_admin_role` (`role`),
                         KEY `idx_admin_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 4. 商品分类表
CREATE TABLE `product_category` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                    `name` varchar(100) NOT NULL COMMENT '分类名称',
                                    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父分类ID（0-一级分类）',
                                    `sort` int NOT NULL DEFAULT 0 COMMENT '排序（值越小越靠前）',
                                    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_parent_id` (`parent_id`),
                                    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 5. 商品表
CREATE TABLE `product` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                           `name` varchar(200) NOT NULL COMMENT '商品名称',
                           `category_id` bigint NOT NULL COMMENT '关联分类ID',
                           `price` decimal(10,2) NOT NULL COMMENT '商品单价（元）',
                           `stock` int NOT NULL DEFAULT 0 COMMENT '库存数量',
                           `sales` int NOT NULL DEFAULT 0 COMMENT '销售数量',
                           `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
                           `description` text COMMENT '商品描述（富文本）',
                           `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
                           `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_category_id` (`category_id`),
                           KEY `idx_status` (`status`),
                           KEY `idx_name` (`name`) COMMENT '支持商品名称搜索',
                           CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 6. 商品图片表
CREATE TABLE `product_image` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
                                 `product_id` bigint NOT NULL COMMENT '关联商品ID',
                                 `image_url` varchar(255) NOT NULL COMMENT '图片URL',
                                 `sort` int NOT NULL DEFAULT 0 COMMENT '排序（值越小越靠前）',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_product_id` (`product_id`),
                                 CONSTRAINT `fk_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- 7. 商品规格表
CREATE TABLE `product_spec` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
                                `product_id` bigint NOT NULL COMMENT '关联商品ID',
                                `spec_name` varchar(50) NOT NULL COMMENT '规格名称（如颜色、尺寸）',
                                `spec_value` varchar(50) NOT NULL COMMENT '规格值（如红色、XL）',
                                `price_offset` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格偏移（+10元/-5元）',
                                `stock` int NOT NULL DEFAULT 0 COMMENT '该规格库存',
                                PRIMARY KEY (`id`),
                                KEY `idx_product_id` (`product_id`),
                                CONSTRAINT `fk_spec_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格表';

-- 8. 购物车表
CREATE TABLE `shopping_cart` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
                                 `user_id` bigint NOT NULL COMMENT '关联用户ID',
                                 `product_id` bigint NOT NULL COMMENT '关联商品ID',
                                 `spec_id` bigint DEFAULT NULL COMMENT '关联规格ID（可为null）',
                                 `quantity` int NOT NULL DEFAULT 1 COMMENT '商品数量',
                                 `checked` tinyint NOT NULL DEFAULT 1 COMMENT '是否选中（0-否，1-是）',
                                 `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                                 `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 KEY `idx_product_id` (`product_id`),
                                 KEY `idx_checked` (`checked`),
                                 CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 9. 订单表
CREATE TABLE `order` (
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
                         `receiver_name` varchar(50) NOT NULL COMMENT '收件人姓名',
                         `receiver_phone` varchar(20) NOT NULL COMMENT '收件人电话',
                         `receiver_address` varchar(255) NOT NULL COMMENT '收件人完整地址',
                         `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
                         `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_order_no` (`order_no`),
                         KEY `idx_user_id` (`user_id`),
                         KEY `idx_status` (`status`),
                         KEY `idx_created_time` (`created_time`),
                         CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 10. 订单项表
CREATE TABLE `order_item` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
                              `order_id` bigint NOT NULL COMMENT '关联订单ID',
                              `product_id` bigint NOT NULL COMMENT '关联商品ID',
                              `product_name` varchar(200) NOT NULL COMMENT '商品名称（冗余）',
                              `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片（冗余）',
                              `spec_info` varchar(100) DEFAULT NULL COMMENT '规格信息（如“颜色：红色”）',
                              `unit_price` decimal(10,2) NOT NULL COMMENT '购买时单价（元）',
                              `quantity` int NOT NULL COMMENT '购买数量',
                              `total_price` decimal(10,2) NOT NULL COMMENT '小计金额（元）',
                              PRIMARY KEY (`id`),
                              KEY `idx_order_id` (`order_id`),
                              KEY `idx_product_id` (`product_id`),
                              CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE,
                              CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- 11. 退款表
CREATE TABLE `refund` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '退款ID',
                          `order_id` bigint NOT NULL COMMENT '关联订单ID',
                          `refund_no` varchar(50) NOT NULL COMMENT '退款单号（唯一）',
                          `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额（元）',
                          `reason` varchar(500) NOT NULL COMMENT '退款原因',
                          `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-申请中，1-已同意，2-已拒绝）',
                          `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                          `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_refund_no` (`refund_no`),
                          KEY `idx_order_id` (`order_id`),
                          KEY `idx_status` (`status`),
                          CONSTRAINT `fk_refund_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款表';

-- 12. 优惠券表（扩展）
CREATE TABLE `coupon` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
                          `name` varchar(100) NOT NULL COMMENT '优惠券名称',
                          `type` tinyint NOT NULL COMMENT '类型（1-满减券，2-折扣券）',
                          `value` decimal(10,2) NOT NULL COMMENT '优惠值（满减金额/折扣率）',
                          `min_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '满减门槛（元）',
                          `start_time` datetime NOT NULL COMMENT '生效时间',
                          `end_time` datetime NOT NULL COMMENT '过期时间',
                          PRIMARY KEY (`id`),
                          KEY `idx_time` (`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券表';

-- 13. 用户优惠券关联表（扩展）
CREATE TABLE `user_coupon` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `user_id` bigint NOT NULL COMMENT '关联用户ID',
                               `coupon_id` bigint NOT NULL COMMENT '关联优惠券ID',
                               `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-未使用，1-已使用，2-已过期）',
                               `get_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_coupon_id` (`coupon_id`),
                               KEY `idx_status` (`status`),
                               CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                               CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券关联表';