CREATE TABLE users (
    uuid UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) UNIQUE,
    city varchar(255) NOT NULL,
    rate NUMERIC(10, 2) NOT NULL,
    role VARCHAR(10) CHECK (role IN ('USER', 'ADMIN')) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE, -- Indica si el registro ha sido eliminado (soft delete), por defecto es FALSE
    deleted_at TIMESTAMP, -- Marca de tiempo de cuándo fue eliminado el registro, puede ser nulo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de cuándo fue creado el registro, por defecto se establece a la hora actual
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Marca de tiempo de cuándo fue actualizado el registro, se actualizará mediante un trigger
);