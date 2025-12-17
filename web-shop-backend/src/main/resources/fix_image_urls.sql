-- 修复数据库中的图片URL，将localhost:8080的完整URL改为相对路径
-- 适配Docker部署环境

-- 更新商品表中的图片URL
UPDATE product 
SET images = REPLACE(images, 'http://localhost:8080/uploads/', '/uploads/')
WHERE images LIKE '%http://localhost:8080/uploads/%';

-- 如果有其他端口的情况也一并处理
UPDATE product 
SET images = REPLACE(images, 'http://localhost:', '/uploads/')
WHERE images LIKE '%http://localhost:%/uploads/%';

-- 清理可能的多余路径
UPDATE product 
SET images = REPLACE(images, '/uploads//uploads/', '/uploads/')
WHERE images LIKE '%/uploads//uploads/%';

-- 显示更新结果
SELECT COUNT(*) as updated_count FROM product WHERE images LIKE '%/uploads/%';