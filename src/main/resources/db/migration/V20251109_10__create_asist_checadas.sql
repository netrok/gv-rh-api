-- Checadas (ENT/SAL) por empleado
CREATE TABLE IF NOT EXISTS asist_checadas (
    id           BIGSERIAL PRIMARY KEY,
    empleado_id  BIGINT       NOT NULL,
    tipo         VARCHAR(3)   NOT NULL,        -- 'ENT' | 'SAL'
    fecha_hora   TIMESTAMP    NOT NULL,        -- LocalDateTime (sin TZ)
    dispositivo  VARCHAR(80),
    ubicacion    VARCHAR(120),
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Índices
CREATE INDEX IF NOT EXISTS ix_checada_empleado ON asist_checadas (empleado_id);
CREATE INDEX IF NOT EXISTS ix_checada_fecha    ON asist_checadas (fecha_hora);
CREATE INDEX IF NOT EXISTS ix_checada_tipo     ON asist_checadas (tipo);

-- (Opcional) FK a empleados(id) si existe la tabla
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'empleados') THEN
        ALTER TABLE asist_checadas
        ADD CONSTRAINT fk_checada_empleado
        FOREIGN KEY (empleado_id) REFERENCES empleados(id)
        ON UPDATE NO ACTION ON DELETE NO ACTION;
    END IF;
END $$;
