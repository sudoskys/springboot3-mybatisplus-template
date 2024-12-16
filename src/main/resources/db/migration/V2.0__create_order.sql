-- postgresql
CREATE TABLE orders
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT         NOT NULL REFERENCES users(id),
    total_price DECIMAL(10, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE order_items
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES orders(id),
    product_id BIGINT         NOT NULL REFERENCES products(id),
    quantity   INTEGER        NOT NULL,
    price      DECIMAL(10, 2) NOT NULL
);

-- Insert test data into the orders table if the table is empty
INSERT INTO orders (user_id, total_price, status)
VALUES (1, 50.00, 'PENDING'),
       (1, 30.00, 'COMPLETED');

-- Insert test data into the order_items table if the table is empty
INSERT INTO order_items (order_id, product_id, quantity, price)
VALUES (1, 1, 2, 20.00),
       (1, 2, 1, 10.00),
       (2, 3, 1, 30.00);