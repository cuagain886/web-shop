-- 创建公告表
CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    title VARCHAR(255) NOT NULL COMMENT '公告标题',
    content LONGTEXT NOT NULL COMMENT '公告内容',
    type INT DEFAULT 1 COMMENT '公告类型（1-普通公告，2-重要公告，3-紧急公告）',
    is_top INT DEFAULT 0 COMMENT '是否置顶（0-否，1-是）',
    status INT DEFAULT 0 COMMENT '状态（0-草稿，1-发布）',
    deleted INT DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_is_top (is_top),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- 插入示例数据
INSERT INTO announcement (title, content, type, is_top, status) VALUES
('欢迎来到WebShop', '欢迎使用WebShop在线购物平台，我们为您提供优质的商品和服务。', 1, 1, 1),
('系统维护通知', '系统将于本周日凌晨2点进行维护，预计维护时间为2小时，期间无法访问。', 2, 0, 1),
('新品上市', '本周新上市多款热销商品，欢迎浏览选购。', 1, 0, 1);
