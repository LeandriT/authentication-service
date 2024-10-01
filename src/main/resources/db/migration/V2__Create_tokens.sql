CREATE TABLE tokens (
    uuid UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    is_logged_out BOOLEAN NOT NULL,
    user_uuid UUID,
    is_deleted BOOLEAN DEFAULT FALSE, -- Indica si el registro ha sido eliminado (soft delete), por defecto es FALSE
    deleted_at TIMESTAMP, -- Marca de tiempo de cuándo fue eliminado el registro, puede ser nulo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de cuándo fue creado el registro, por defecto se establece a la hora actual
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de cuándo fue actualizado el registro, se actualizará mediante un trigger
    FOREIGN KEY (user_uuid) REFERENCES users(uuid) -- Agregar la referencia a la tabla users
);
