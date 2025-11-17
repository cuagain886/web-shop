SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE browsing_history;
TRUNCATE TABLE operation_log;
TRUNCATE TABLE orders;
TRUNCATE TABLE order_item;
TRUNCATE TABLE product;
TRUNCATE TABLE product_review;
TRUNCATE TABLE refund;
TRUNCATE TABLE shopping_cart;
TRUNCATE TABLE user_favorite;
TRUNCATE TABLE user_address;
TRUNCATE TABLE product_sku;
-- 重置自增ID
ALTER TABLE `product_category` AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;