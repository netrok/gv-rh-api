CREATE TABLE IF NOT EXISTS approval_flow (
    id BIGSERIAL PRIMARY KEY,
    modulo VARCHAR(60) NOT NULL,
    entidad_id VARCHAR(80) NOT NULL
);
CREATE INDEX IF NOT EXISTS ix_flow_modulo_entidad ON approval_flow(modulo, entidad_id);

CREATE TABLE IF NOT EXISTS approval_step (
    id BIGSERIAL PRIMARY KEY,
    flow_id BIGINT NOT NULL REFERENCES approval_flow(id) ON DELETE CASCADE,
    orden INT NOT NULL,
    aprobador VARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_decision TIMESTAMPTZ,
    comentario TEXT
);
