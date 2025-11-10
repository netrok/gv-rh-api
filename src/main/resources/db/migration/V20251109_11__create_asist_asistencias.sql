-- Asistencias diarias por empleado
CREATE TABLE IF NOT EXISTS asist_asistencias (
    id                     BIGSERIAL PRIMARY KEY,
    empleado_id            BIGINT       NOT NULL,
    fecha                  DATE         NOT NULL,
    hora_entrada           TIME,
    hora_salida            TIME,
    retardo_min            INTEGER,
    salida_anticipada_min  INTEGER,
    estado                 VARCHAR(12)  NOT NULL,  -- NORMAL | RETARDO | FALTA | INCOMPLETA
    origen                 VARCHAR(30)  NOT NULL DEFAULT 'manual',
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uc_asistencia_emp_fecha UNIQUE (empleado_id, fecha)
);

-- Índices
CREATE INDEX IF NOT EXISTS ix_asistencia_empleado ON asist_asistencias (empleado_id);
CREATE INDEX IF NOT EXISTS ix_asistencia_fecha    ON asist_asistencias (fecha);
CREATE INDEX IF NOT EXISTS ix_asistencia_estado   ON asist_asistencias (estado);

-- (Opcional) FK a empleados(id) si existe la tabla
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'empleados') THEN
        ALTER TABLE asist_asistencias
        ADD CONSTRAINT fk_asistencia_empleado
        FOREIGN KEY (empleado_id) REFERENCES empleados(id)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
    END IF;
END $$;
