-- postgresql
CREATE TABLE IF NOT EXISTS products
(
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    image_url   VARCHAR(255)   NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    description TEXT           NOT NULL,
    uploader_id BIGINT         NOT NULL,
    upload_time TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_id    BIGINT,
    tags        VARCHAR(255)
);

-- 创建用户
-- postgresql
CREATE TABLE IF NOT EXISTS users
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

