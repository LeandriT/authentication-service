CREATE TABLE user_preferences (
    uuid UUID PRIMARY KEY,
    parameter_key VARCHAR(100) NOT NULL,
    parameter_value VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    user_uuid UUID NOT NULL,
    --EXTRA INFO
    is_deleted BOOLEAN DEFAULT FALSE, -- Indica si el registro ha sido eliminado (soft delete)
    deleted_at TIMESTAMP WITH TIME ZONE, -- Marca de tiempo de cuándo fue eliminado el registro
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de creación
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de actualización
    FOREIGN KEY (user_uuid) REFERENCES users(uuid)
);
