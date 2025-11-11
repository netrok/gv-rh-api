-- V20251110.42__create_incidencias.sql
CREATE TABLE IF NOT EXISTS incidencias (
  id           BIGSERIAL PRIMARY KEY,
  empleado_id  BIGINT       NOT NULL,
  tipo         VARCHAR(30)  NOT NULL,
  estado       VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
  fecha        DATE         NOT NULL,
  minutos      INTEGER,
  motivo       VARCHAR(500),
  adjunto      VARCHAR(255),
  created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS ix_incidencias_empleado_id ON incidencias(empleado_id);
CREATE INDEX IF NOT EXISTS ix_incidencias_estado ON incidencias(estado);
CREATE INDEX IF NOT EXISTS ix_incidencias_tipo ON incidencias(tipo);
