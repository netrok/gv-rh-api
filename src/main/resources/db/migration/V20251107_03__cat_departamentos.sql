CREATE TABLE IF NOT EXISTS cat_departamentos (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS ix_dep_nombre ON cat_departamentos (nombre);
CREATE INDEX IF NOT EXISTS ix_dep_activo  ON cat_departamentos (activo);