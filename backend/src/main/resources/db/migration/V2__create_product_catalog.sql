CREATE TABLE category (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    CONSTRAINT uq_category_name UNIQUE (name),
    CONSTRAINT uq_category_slug UNIQUE (slug)
);

CREATE TABLE product (
    id UUID PRIMARY KEY,
    seller_id UUID NOT NULL REFERENCES app_user (id) ON DELETE RESTRICT,
    category_id UUID NOT NULL REFERENCES category (id) ON DELETE RESTRICT,
    name VARCHAR(160) NOT NULL,
    slug VARCHAR(180) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_product_slug UNIQUE (slug),
    CONSTRAINT ck_product_price CHECK (price >= 0),
    CONSTRAINT ck_product_stock CHECK (stock_quantity >= 0),
    CONSTRAINT ck_product_status CHECK (status IN ('DRAFT', 'ACTIVE', 'ARCHIVED'))
);

CREATE INDEX ix_product_catalog_browse
    ON product (status, category_id, created_at DESC);

CREATE INDEX ix_product_seller
    ON product (seller_id, status, created_at DESC);

INSERT INTO category (id, name, slug) VALUES
    ('00000000-0000-0000-0000-000000000101', 'Electronics', 'electronics'),
    ('00000000-0000-0000-0000-000000000102', 'Fashion', 'fashion'),
    ('00000000-0000-0000-0000-000000000103', 'Home & Living', 'home-living');
