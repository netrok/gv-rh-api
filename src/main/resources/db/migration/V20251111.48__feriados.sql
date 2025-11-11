CREATE TABLE IF NOT EXISTS feriados (
  id          BIGSERIAL PRIMARY KEY,
  fecha       DATE        NOT NULL UNIQUE,
  descripcion VARCHAR(120) NOT NULL
);
CREATE INDEX IF NOT EXISTS ix_feriados_fecha ON feriados(fecha);
