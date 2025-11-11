-- Crea tabla de roles y seed de admin (compatible con tu seed anterior)
-- PostgreSQL / Flyway SQL

-- 1) Extensión para BCrypt (si no existe)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2) Asegura tabla users (no toca si ya existe)
--   *Si ya tienes otra definición de users, esto no la altera.
CREATE TABLE IF NOT EXISTS users (
  username text PRIMARY KEY,
  password text NOT NULL,
  enabled  boolean NOT NULL DEFAULT true
);

-- 3) Crea tabla de roles simple (username/role)
CREATE TABLE IF NOT EXISTS user_roles (
  username text NOT NULL REFERENCES users(username) ON DELETE CASCADE,
  role     varchar(50) NOT NULL,
  CONSTRAINT pk_user_roles PRIMARY KEY (username, role)
);

-- 4) Índice útil (opcional, por si haces filtros por rol)
CREATE INDEX IF NOT EXISTS idx_user_roles_role ON user_roles(role);

-- 5) Admin por defecto (misma contraseña que tu seed anterior)
INSERT INTO users (username, password, enabled)
VALUES ('netrok', crypt('F3n1xh311*', gen_salt('bf')), true)
ON CONFLICT (username) DO NOTHING;

-- 6) Rol de admin
INSERT INTO user_roles (username, role)
VALUES ('netrok', 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;
