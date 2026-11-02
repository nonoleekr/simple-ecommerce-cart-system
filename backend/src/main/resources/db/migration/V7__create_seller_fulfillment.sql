CREATE TABLE seller_fulfillment (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES customer_order (id) ON DELETE CASCADE,
    seller_id UUID NOT NULL REFERENCES app_user (id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_fulfillment_order_seller UNIQUE (order_id, seller_id),
    CONSTRAINT ck_fulfillment_status CHECK (status IN ('CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

CREATE INDEX ix_fulfillment_seller_status
    ON seller_fulfillment (seller_id, status, updated_at DESC);

INSERT INTO seller_fulfillment (id, order_id, seller_id, status, updated_at)
SELECT
    md5(oi.order_id::text || p.seller_id::text)::uuid,
    oi.order_id,
    p.seller_id,
    o.status,
    o.updated_at
FROM order_item oi
JOIN product p ON p.id = oi.product_id
JOIN customer_order o ON o.id = oi.order_id
GROUP BY oi.order_id, p.seller_id, o.status, o.updated_at;
