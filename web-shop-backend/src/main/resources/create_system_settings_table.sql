-- 创建系统设置表
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设置ID',
    site_name VARCHAR(100) DEFAULT '购物平台' COMMENT '网站名称',
    site_description VARCHAR(500) COMMENT '网站描述',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    order_cancel_time INT DEFAULT 24 COMMENT '自动取消订单时间（小时）',
    order_confirm_time INT DEFAULT 7 COMMENT '自动确认收货时间（天）',
    stock_warning INT DEFAULT 10 COMMENT '库存预警数量',
    default_shipping DECIMAL(10, 2) DEFAULT 10.00 COMMENT '默认运费',
    allow_register BOOLEAN DEFAULT TRUE COMMENT '是否允许用户注册',
    allow_comment BOOLEAN DEFAULT TRUE COMMENT '是否允许商品评价',
    maintenance_mode BOOLEAN DEFAULT FALSE COMMENT '是否开启维护模式',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- 插入默认设置
INSERT INTO system_settings (
    site_name, 
    site_description, 
    contact_phone, 
    contact_email, 
    order_cancel_time, 
    order_confirm_time, 
    stock_warning, 
    default_shipping, 
    allow_register, 
    allow_comment, 
    maintenance_mode
) VALUES (
    '购物平台',
    '一个基于Vue3的现代化购物平台',
    '400-888-8888',
    'contact@example.com',
    24,
    7,
    10,
    10.00,
    TRUE,
    TRUE,
    FALSE
) ON DUPLICATE KEY UPDATE updated_time = CURRENT_TIMESTAMP;
