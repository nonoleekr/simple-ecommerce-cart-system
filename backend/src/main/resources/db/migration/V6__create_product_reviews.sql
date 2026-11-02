CREATE TABLE product_review (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    rating SMALLINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    comment VARCHAR(2000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_review_product_customer UNIQUE (product_id, customer_id),
    CONSTRAINT ck_review_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX ix_review_product_created
    ON product_review (product_id, created_at DESC);
