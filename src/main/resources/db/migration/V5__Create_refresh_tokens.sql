CREATE TABLE refresh_tokens (
    uuid UUID PRIMARY KEY,
    token_uuid VARCHAR(255) NOT NULL, -- Token del refresh token
    expiration_date TIMESTAMP WITH TIME ZONE NOT NULL, -- Fecha de expiración
    user_uuid UUID NOT NULL, -- ID del usuario asociado

    is_deleted BOOLEAN DEFAULT FALSE, -- Indica si el registro ha sido eliminado (soft delete), por defecto es FALSE
    deleted_at TIMESTAMP WITH TIME ZONE, -- Marca de tiempo de cuándo fue eliminado el registro, puede ser nulo
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de cuándo fue creado el registro, por defecto se establece a la hora actual
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- Marca de tiempo de cuándo fue actualizado el registro, se actualizará mediante un trigger

    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_uuid) REFERENCES users(uuid) -- Clave foránea
);