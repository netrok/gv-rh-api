CREATE TABLE IF NOT EXISTS periodos_vacacionales (
  id           BIGSERIAL PRIMARY KEY,
  anio         INT NOT NULL UNIQUE,
  fecha_inicio DATE NOT NULL,
  fecha_fin    DATE NOT NULL,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS saldos_vacaciones (
  id             BIGSERIAL PRIMARY KEY,
  empleado_id    BIGINT NOT NULL,
  anio           INT    NOT NULL,
  dias_asignados INT    NOT NULL DEFAULT 0,
  dias_disfrutados INT  NOT NULL DEFAULT 0,
  dias_pendientes  INT  NOT NULL DEFAULT 0,
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (empleado_id, anio)
);

CREATE INDEX IF NOT EXISTS ix_saldos_empleado_anio ON saldos_vacaciones(empleado_id, anio);

CREATE TABLE IF NOT EXISTS solicitudes_vacaciones (
  id            BIGSERIAL PRIMARY KEY,
  empleado_id   BIGINT      NOT NULL,
  anio          INT         NOT NULL,
  fecha_desde   DATE        NOT NULL,
  fecha_hasta   DATE        NOT NULL,
  dias          INT         NOT NULL,
  estado        VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  motivo        VARCHAR(500),
  adjunto       VARCHAR(255),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS ix_solicitudes_empleado ON solicitudes_vacaciones(empleado_id);
CREATE INDEX IF NOT EXISTS ix_solicitudes_estado   ON solicitudes_vacaciones(estado);
