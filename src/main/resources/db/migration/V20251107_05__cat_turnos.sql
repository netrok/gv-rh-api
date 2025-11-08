CREATE TABLE IF NOT EXISTS cat_turnos (
                                          id BIGSERIAL PRIMARY KEY,
                                          nombre VARCHAR(80) NOT NULL UNIQUE,
    hora_entrada TIME NOT NULL,
    hora_salida  TIME NOT NULL,
    tolerancia_entrada_min SMALLINT NOT NULL DEFAULT 0,
    tolerancia_salida_min  SMALLINT NOT NULL DEFAULT 0,
    ventana_inicio_min     SMALLINT NOT NULL DEFAULT 180,
    ventana_fin_min        SMALLINT NOT NULL DEFAULT 180,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS ix_turno_nombre ON cat_turnos (nombre);
CREATE INDEX IF NOT EXISTS ix_turno_activo ON cat_turnos (activo);