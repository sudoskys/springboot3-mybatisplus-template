-- postgresql
CREATE TABLE products
(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    image_url   VARCHAR(255)   NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    description TEXT           NOT NULL,
    uploader_id BIGINT         NOT NULL,
    upload_time TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_id    jsonb,
    tags        VARCHAR(255)
);

-- 创建用户
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL DEFAULT 'USER'
);

-- Create a check constraint to limit the values of role if it does not already exist
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.table_constraints
                       WHERE constraint_type = 'CHECK'
                         AND table_name = 'users'
                         AND constraint_name = 'check_role') THEN
            ALTER TABLE users
                ADD CONSTRAINT check_role CHECK (role IN ('USER', 'ADMIN'));
        END IF;
    END
$$;

-- Insert test data into the products table if the table is empty
INSERT INTO products (id, name, image_url, price, description, uploader_id, event_id, tags)
VALUES (1, '香蕉水果蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 10.00,
        '香蕉风味的水果蛋糕，好吃带回家～', 1, NULL, 'tag1,tag2'),
       (2, '草莓水果蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 12.00,
        '草莓风味的水果蛋糕，好吃带回家～', 1, NULL, 'tag1,tag2'),
       (3, '榴莲水果蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 15.00,
        '榴莲风味的水果蛋糕，好吃带回家～', 1, NULL, 'tag1,tag2'),
       (4, '法式烤面包', 'https://via.placeholder.com/500x300?text=Bread+Item', 8.00,
        '美味的法式烤面包，香脆可口～', 2, NULL, 'tag3,tag4'),
       (5, '巧克力蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 20.00,
        '浓郁的巧克力蛋糕，甜蜜享受～', 2, NULL, 'tag1,tag5'),
       (6, '抹茶蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 18.00,
        '清新的抹茶蛋糕，回味无穷～', 3, NULL, 'tag2,tag6'),
       (7, '蓝莓芝士蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 22.00,
        '丰富的蓝莓与芝士蛋糕的完美结合～', 3, NULL, 'tag4,tag7'),
       (8, '香橙马芬', 'https://via.placeholder.com/500x300?text=Muffin+Item', 5.00,
        '香橙味的马芬蛋糕，新鲜出炉！', 4, NULL, 'tag3,tag8'),
       (9, '咖啡味蛋糕', 'https://via.placeholder.com/500x300?text=Cake+Item', 19.00,
        '浓郁咖啡爱好者的最爱！', 4, NULL, 'tag5,tag9'),
       (10, '核桃麦芬', 'https://via.placeholder.com/500x300?text=Muffin+Item', 6.00,
        '脆香的核桃与松软的麦芬完美融合！', 5, NULL, 'tag6,tag8'),
       (11, '蜂蜜吐司', 'https://via.placeholder.com/500x300?text=Bread+Item', 8.50,
        '甜蜜的蜂蜜吐司，早餐的最佳选择！', 5, NULL, 'tag3,tag10');

-- Insert test data into the users table if the table is empty
INSERT INTO users (id, email, password, role)
VALUES (1, 'demo@gmail.com', 'passwords', 'ADMIN');
