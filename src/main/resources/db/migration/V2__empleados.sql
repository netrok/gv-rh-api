-- Tabla empleados
CREATE TABLE IF NOT EXISTS empleados (
                                         id BIGSERIAL PRIMARY KEY,
                                         num_empleado VARCHAR(30) NOT NULL UNIQUE,
    nombres VARCHAR(120) NOT NULL,
    apellido_paterno VARCHAR(120),
    apellido_materno VARCHAR(120),
    email VARCHAR(160),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- Índices útiles (además del UNIQUE en num_empleado)
CREATE INDEX IF NOT EXISTS ix_empleados_activo ON empleados (activo);
CREATE INDEX IF NOT EXISTS ix_empleados_nombres ON empleados (nombres);

-- Trigger para mantener updated_at (opcional pero recomendado)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_proc WHERE proname = 'empleados_set_updated_at') THEN
        CREATE OR REPLACE FUNCTION empleados_set_updated_at()
        RETURNS TRIGGER AS $f$
BEGIN
            NEW.updated_at = NOW();
RETURN NEW;
END;
        $f$ LANGUAGE plpgsql;
END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'trg_empleados_set_updated_at'
    ) THEN
CREATE TRIGGER trg_empleados_set_updated_at
    BEFORE UPDATE ON empleados
    FOR EACH ROW
    EXECUTE FUNCTION empleados_set_updated_at();
END IF;
END$$;
