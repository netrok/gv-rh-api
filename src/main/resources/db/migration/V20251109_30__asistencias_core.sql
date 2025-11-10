-- ===========================================
-- GV-RH • Módulo Asistencias (core)
-- Tablas: asist_checadas, asist_asistencias
-- ===========================================

-- ---------- Tabla: asist_checadas ----------
CREATE TABLE IF NOT EXISTS asist_checadas (
    id              BIGSERIAL PRIMARY KEY,
    empleado_id     BIGINT          NOT NULL,
    tipo            VARCHAR(3)      NOT NULL,     -- ENT | SAL
    fecha_hora      TIMESTAMP       NOT NULL,
    dispositivo     VARCHAR(80),
    ubicacion       VARCHAR(120),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Índices para performance de consultas por empleado/fecha/tipo
CREATE INDEX IF NOT EXISTS ix_checada_empleado ON asist_checadas (empleado_id);
CREATE INDEX IF NOT EXISTS ix_checada_fecha    ON asist_checadas (fecha_hora);
CREATE INDEX IF NOT EXISTS ix_checada_tipo     ON asist_checadas (tipo);

-- FK opcional hacia empleados(id) si existe la tabla
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'empleados'
    ) THEN
        -- Evitar duplicar la FK si ya está creada
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'fk_checadas_empleado'
        ) THEN
            ALTER TABLE asist_checadas
            ADD CONSTRAINT fk_checadas_empleado
            FOREIGN KEY (empleado_id) REFERENCES empleados(id);
        END IF;
    END IF;
END$$;

-- ---------- Tabla: asist_asistencias ----------
CREATE TABLE IF NOT EXISTS asist_asistencias (
    id                      BIGSERIAL PRIMARY KEY,
    empleado_id             BIGINT          NOT NULL,
    fecha                   DATE            NOT NULL,
    hora_entrada            TIME,
    hora_salida             TIME,
    retardo_min             INTEGER,
    salida_anticipada_min   INTEGER,
    estado                  VARCHAR(12)     NOT NULL DEFAULT 'NORMAL', -- NORMAL | RETARDO | FALTA | INCOMPLETA
    origen                  VARCHAR(30)     NOT NULL DEFAULT 'manual',
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT uc_asistencia_emp_fecha UNIQUE (empleado_id, fecha)
);

-- Índices
CREATE INDEX IF NOT EXISTS ix_asistencia_empleado ON asist_asistencias (empleado_id);
CREATE INDEX IF NOT EXISTS ix_asistencia_fecha    ON asist_asistencias (fecha);
CREATE INDEX IF NOT EXISTS ix_asistencia_estado   ON asist_asistencias (estado);

-- FK opcional hacia empleados(id)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'public' AND table_name = 'empleados'
    ) THEN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE constraint_name = 'fk_asistencia_empleado'
        ) THEN
            ALTER TABLE asist_asistencias
            ADD CONSTRAINT fk_asistencia_empleado
            FOREIGN KEY (empleado_id) REFERENCES empleados(id);
        END IF;
    END IF;
END$$;
