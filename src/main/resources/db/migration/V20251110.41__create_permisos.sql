CREATE TABLE IF NOT EXISTS permisos (
    id              BIGSERIAL PRIMARY KEY,
    empleado_id     BIGINT       NOT NULL,
    tipo            VARCHAR(20)  NOT NULL,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    fecha_desde     DATE         NOT NULL,
    fecha_hasta     DATE         NOT NULL,
    horas           NUMERIC(5,2),
    motivo          VARCHAR(500),
    adjunto         VARCHAR(255),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_permisos_empleado_id ON permisos(empleado_id);
CREATE INDEX IF NOT EXISTS ix_permisos_estado ON permisos(estado);
