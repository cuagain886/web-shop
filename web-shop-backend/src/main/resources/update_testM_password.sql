USE web_shop;

UPDATE user 
SET password = '$2a$10$4Ts1nEuLKq90Zx8s5bzQLuUXTI6SHcVpBgGKdYjk3Hpoogws5ULIu',
    updated_time = CURRENT_TIMESTAMP
WHERE username = 'testM';

SELECT 
    id,
    username,
    email,
    role,
    status,
    LEFT(password, 30) AS password_preview,
    updated_time
FROM user
WHERE username = 'testM';