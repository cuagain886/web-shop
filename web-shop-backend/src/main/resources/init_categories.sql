SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE browsing_history;
TRUNCATE TABLE operation_log;
TRUNCATE TABLE orders;
TRUNCATE TABLE order_item;

TRUNCATE TABLE product;
TRUNCATE TABLE product_category;
TRUNCATE TABLE product_review;
TRUNCATE TABLE refund;
TRUNCATE TABLE shopping_cart;
TRUNCATE TABLE user_favorite;
TRUNCATE TABLE user_address;
-- 重置自增ID
ALTER TABLE `product_category` AUTO_INCREMENT = 1;

-- 插入首页左侧分类导航栏的分类数据
INSERT INTO `product_category` (`id`, `name`, `parent_id`, `icon`, `sort`, `deleted`, `created_time`, `updated_time`) VALUES
(1, '家用电器', 0, '📺', 1, 0, NOW(), NOW()),
(2, '手机数码', 0, '📱', 2, 0, NOW(), NOW()),
(3, '电脑办公', 0, '💻', 3, 0, NOW(), NOW()),
(4, '家居家具', 0, '🛋️', 4, 0, NOW(), NOW()),
(5, '服装鞋靴', 0, '👕', 5, 0, NOW(), NOW()),
(6, '美妆个护', 0, '💄', 6, 0, NOW(), NOW()),
(7, '运动户外', 0, '⚽', 7, 0, NOW(), NOW()),
(8, '食品生鲜', 0, '🍎', 8, 0, NOW(), NOW()),
(9, '母婴玩具', 0, '🍼', 9, 0, NOW(), NOW()),
(10, '图书文娱', 0, '📚', 10, 0, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;