CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE TABLE "user_manager" (
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
    description VARCHAR(300) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price NUMERIC NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_purchase (
    id UUID PRIMARY key DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    product_id UUID NOT NULL,
    puntage INT NOT NULL,
    price_buyed NUMERIC NOT NULL,
    total_buyed INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Clave foránea para la tabla user
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_manager (id),

    -- Clave foránea para la tabla product
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id)
);