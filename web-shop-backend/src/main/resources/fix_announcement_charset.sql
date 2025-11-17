-- 修复公告表字符集和数据
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 清空现有数据
DELETE FROM announcement;

-- 重新插入数据
INSERT INTO announcement (title, content, type, is_top, status) VALUES 
('欢迎来到WebShop', '欢迎使用WebShop在线购物平台，我们为您提供优质的商品和服务。', 1, 1, 1),
('系统维护通知', '系统将于本周日凌晨2点进行维护，预计维护时间为2小时，期间无法访问。', 2, 0, 1),
('新品上市', '本周新上市多款热销商品，欢迎浏览选购。', 1, 0, 1);
