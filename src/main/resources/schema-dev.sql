CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS  "user_manager" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Genera un UUID automáticamente
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role_type VARCHAR(50) DEFAULT 'common', -- Campo para tipo de rol, valor por defecto 'common'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha y hora de creación
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha y hora de última actualización
    deleted_at TIMESTAMP DEFAULT NULL -- Fecha y hora de eliminación, por defecto NULL
);

CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(3000) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price NUMERIC NOT NULL,
    external_item_id VARCHAR(1000) NOT NULL;
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_purchase (
    id UUID PRIMARY key DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    puntage NUMERIC NOT NULL,
    price_buyed NUMERIC NOT NULL,
    total_buyed NUMERIC NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Clave foránea para la tabla user
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_manager (id),

    -- Clave foránea para la tabla product
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE TABLE IF NOT EXISTS product_favorite (
    product_favorite_id UUID PRIMARY key DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    is_favorite BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Clave foránea para la tabla user
    CONSTRAINT fk_user_product_favorite FOREIGN KEY (user_id) REFERENCES user_manager (id),

    -- Clave foránea para la tabla product
    CONSTRAINT fk_product_favorite FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE TABLE IF NOT EXISTS product_comment (
    comment_id UUID PRIMARY key DEFAULT gen_random_uuid(),
    product_details_id UUID NOT NULL,
    description VARCHAR(3000) NOT NULL,
    likes NUMERIC NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Clave foránea para la tabla product_purchase
    CONSTRAINT fk_product_purchase FOREIGN KEY (product_details_id) REFERENCES product_purchase (id)
);