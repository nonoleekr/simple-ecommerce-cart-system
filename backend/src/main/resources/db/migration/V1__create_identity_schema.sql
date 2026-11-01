CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_user_username UNIQUE (username),
    CONSTRAINT uq_app_user_email UNIQUE (email),
    CONSTRAINT ck_app_user_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DISABLED'))
);

CREATE TABLE role (
    id SMALLSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    CONSTRAINT uq_role_name UNIQUE (name),
    CONSTRAINT ck_role_name CHECK (name IN ('CUSTOMER', 'SELLER', 'ADMIN'))
);

CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    role_id SMALLINT NOT NULL REFERENCES role (id) ON DELETE RESTRICT,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO role (name) VALUES ('CUSTOMER'), ('SELLER'), ('ADMIN');
