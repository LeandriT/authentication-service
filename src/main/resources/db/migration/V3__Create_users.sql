CREATE TABLE users (
    uuid UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    birth_day DATE NOT NULL,
    rate NUMERIC(10, 2) NOT NULL,
    role VARCHAR(10) CHECK (role IN ('USER', 'ADMIN')) NOT NULL,
    username VARCHAR(100) NOT NULL,
    location_uuid uuid NOT NULL,
    latitude NUMERIC(9, 6) NOT NULL,
    longitude NUMERIC(9, 6) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_locations FOREIGN KEY (location_uuid) REFERENCES locations (uuid)
);