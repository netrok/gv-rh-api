-- Crea lo básico para auth antes del seed
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tabla users (ajusta si ya la tienes con otros campos)
CREATE TABLE IF NOT EXISTS users (
  username text PRIMARY KEY,
  password text NOT NULL,
  enabled  boolean NOT NULL DEFAULT true
);

-- Tabla de roles simple por username
CREATE TABLE IF NOT EXISTS user_roles (
  username text NOT NULL REFERENCES users(username) ON DELETE CASCADE,
  role     varchar(50) NOT NULL,
  CONSTRAINT pk_user_roles PRIMARY KEY (username, role)
);

CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role);
