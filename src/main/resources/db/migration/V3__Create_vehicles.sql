CREATE TABLE vehicles (
    uuid UUID PRIMARY KEY,
    license_plate VARCHAR(50) NOT NULL, -- Placa del vehículo
    dni VARCHAR(20), -- Identificación del usuario
    full_name VARCHAR(255), -- Identificación del usuario
    phone_number VARCHAR(15) NOT NULL, -- Teléfono celular del usuario
    secondary_phone_number VARCHAR(15), -- Segundo teléfono opcional
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Fecha de registro del vehículo
    parked_time BIGINT default 0, -- Tiempo que ha estado parqueado
    status VARCHAR(20) NOT NULL DEFAULT 'PARQUEADO',
    user_uuid UUID NOT NULL,
    amount_charged NUMERIC(10,2) DEFAULT 0.0,
    rate NUMERIC(10,2) DEFAULT 0.0,
    is_deleted BOOLEAN DEFAULT FALSE, -- Indica si el registro ha sido eliminado (soft delete)
    deleted_at TIMESTAMP, -- Marca de tiempo de cuándo fue eliminado el registro
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de creación
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de actualización
    FOREIGN KEY (user_uuid) REFERENCES users(uuid)
);
