ALTER TABLE product ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE customer_order (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES app_user (id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    address_line1 VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_customer_order_status
        CHECK (status IN ('CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT ck_customer_order_total CHECK (total_amount >= 0)
);

CREATE TABLE order_item (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES customer_order (id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product (id) ON DELETE RESTRICT,
    product_name VARCHAR(160) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    CONSTRAINT ck_order_item_price CHECK (unit_price >= 0),
    CONSTRAINT ck_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT ck_order_item_total CHECK (line_total >= 0)
);

CREATE INDEX ix_customer_order_customer_created
    ON customer_order (customer_id, created_at DESC);

CREATE INDEX ix_order_item_order ON order_item (order_id);
