-- src/main/resources/db/migration/V20251111.60__seed_admin.sql
-- ADAPTA nombres de tabla/campos según tu entidad (p.ej. user_account / users)
-- Requiere pgcrypto para BCrypt
CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (username, password, enabled)
VALUES ('netrok', crypt('F3n1xh311*', gen_salt('bf')), true)
ON CONFLICT (username) DO NOTHING;

-- si manejas roles en tabla aparte:
INSERT INTO user_roles (username, role)
VALUES ('netrok', 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;
