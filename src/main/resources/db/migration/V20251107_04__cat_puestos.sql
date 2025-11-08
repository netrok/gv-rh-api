CREATE TABLE IF NOT EXISTS cat_puestos (
                                           id BIGSERIAL PRIMARY KEY,
                                           departamento_id BIGINT NOT NULL,
                                           nombre VARCHAR(120) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uc_puesto_dep_nombre UNIQUE (departamento_id, nombre)
    );

CREATE INDEX IF NOT EXISTS ix_pue_dep     ON cat_puestos (departamento_id);
CREATE INDEX IF NOT EXISTS ix_pue_nombre  ON cat_puestos (nombre);
CREATE INDEX IF NOT EXISTS ix_pue_activo  ON cat_puestos (activo);

ALTER TABLE cat_puestos
    ADD CONSTRAINT fk_puesto_departamento
        FOREIGN KEY (departamento_id) REFERENCES cat_departamentos(id) ON DELETE RESTRICT;