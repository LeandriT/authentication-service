CREATE TABLE locations (
    uuid UUID PRIMARY KEY,            -- Identificador único
    code VARCHAR(20) NOT NULL,        -- Código único de la localidad, por ejemplo, '00'
    parent_code VARCHAR(20),          -- Código del nivel superior, puede ser NULL
    name VARCHAR(100) NOT NULL,       -- Nombre de la localidad, por ejemplo, 'ECUADOR'
    status VARCHAR(10) DEFAULT 'ACTIVO' CHECK (status IN ('ACTIVO', 'INACTIVO')), -- Estado con valores permitidos
    is_deleted BOOLEAN DEFAULT FALSE, -- Indica si el registro ha sido eliminado (soft delete)
    deleted_at TIMESTAMP WITH TIME ZONE, -- Marca de tiempo de cuándo fue eliminado el registro
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de creación
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP -- Marca de tiempo de actualización
);