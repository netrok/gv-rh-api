-- Usuarios, Roles y tabla puente
CREATE TABLE IF NOT EXISTS roles (
                                     id          BIGSERIAL PRIMARY KEY,
                                     name        VARCHAR(40) NOT NULL UNIQUE,      -- e.g. ADMIN, RRHH
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS users (
                                     id          BIGSERIAL PRIMARY KEY,
                                     username    VARCHAR(80) NOT NULL UNIQUE,
    password    VARCHAR(200) NOT NULL,            -- BCrypt
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
    );

-- Índices útiles
CREATE INDEX IF NOT EXISTS ix_users_username ON users(username);
CREATE INDEX IF NOT EXISTS ix_roles_name ON roles(name);
