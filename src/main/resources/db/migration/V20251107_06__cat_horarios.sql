CREATE TABLE IF NOT EXISTS cat_horarios (
                                            id BIGSERIAL PRIMARY KEY,
                                            nombre VARCHAR(120) NOT NULL UNIQUE,
    hora_entrada TIME NOT NULL,
    hora_salida  TIME NOT NULL,
    minutos_comida SMALLINT NOT NULL DEFAULT 0,

    lunes    BOOLEAN NOT NULL DEFAULT TRUE,
    martes   BOOLEAN NOT NULL DEFAULT TRUE,
    miercoles BOOLEAN NOT NULL DEFAULT TRUE,
    jueves   BOOLEAN NOT NULL DEFAULT TRUE,
    viernes  BOOLEAN NOT NULL DEFAULT TRUE,
    sabado   BOOLEAN NOT NULL DEFAULT FALSE,
    domingo  BOOLEAN NOT NULL DEFAULT FALSE,

    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS ix_horario_nombre ON cat_horarios (nombre);
CREATE INDEX IF NOT EXISTS ix_horario_activo ON cat_horarios (activo);