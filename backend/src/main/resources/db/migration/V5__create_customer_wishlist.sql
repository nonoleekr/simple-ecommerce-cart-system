CREATE TABLE wishlist_item (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_wishlist_customer_product UNIQUE (customer_id, product_id)
);

CREATE INDEX ix_wishlist_customer_created
    ON wishlist_item (customer_id, created_at DESC);
