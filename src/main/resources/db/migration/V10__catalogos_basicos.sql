-- ==== CATÁLOGOS BÁSICOS (Departamentos, Puestos) ====

-- 1) Crear tablas si no existen
CREATE TABLE IF NOT EXISTS departamentos (
                                             id   BIGSERIAL PRIMARY KEY,
                                             nombre VARCHAR(120) UNIQUE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS puestos (
                                       id   BIGSERIAL PRIMARY KEY,
                                       nombre VARCHAR(120) UNIQUE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
    );

-- 2) Semillas mínimas (no obligatorias, pero útiles)
INSERT INTO departamentos (nombre)
SELECT 'General'
    WHERE NOT EXISTS (SELECT 1 FROM departamentos WHERE nombre = 'General');

INSERT INTO puestos (nombre)
SELECT 'Empleado'
    WHERE NOT EXISTS (SELECT 1 FROM puestos WHERE nombre = 'Empleado');

-- 3) Asegurar columnas en empleados (por compatibilidad)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='empleados' AND column_name='departamento_id'
  ) THEN
ALTER TABLE empleados ADD COLUMN departamento_id BIGINT;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='empleados' AND column_name='puesto_id'
  ) THEN
ALTER TABLE empleados ADD COLUMN puesto_id BIGINT;
END IF;
END $$;

-- 4) Limpiar referencias huérfanas ANTES de crear FKs
UPDATE empleados e
SET departamento_id = NULL
WHERE departamento_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM departamentos d WHERE d.id = e.departamento_id);

UPDATE empleados e
SET puesto_id = NULL
WHERE puesto_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM puestos p WHERE p.id = e.puesto_id);

-- 5) Crear FKs en modo "NOT VALID" (no valida filas viejas todavía)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname='fk_empleado_departamento'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_empleado_departamento
        FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
            ON UPDATE CASCADE ON DELETE SET NULL
    NOT VALID;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname='fk_empleado_puesto'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_empleado_puesto
        FOREIGN KEY (puesto_id) REFERENCES puestos(id)
            ON UPDATE CASCADE ON DELETE SET NULL
    NOT VALID;
END IF;
END $$;

-- 6) (Opcional) Validar ahora que ya no hay huérfanos
-- Si por alguna razón vuelve a fallar, comenta estas dos líneas y ejecuta luego
-- un UPDATE corrigiendo datos, y más tarde haz VALIDATE manual.
ALTER TABLE empleados VALIDATE CONSTRAINT fk_empleado_departamento;
ALTER TABLE empleados VALIDATE CONSTRAINT fk_empleado_puesto;

-- 7) Índices útiles para joins/filtros
CREATE INDEX IF NOT EXISTS ix_empleados_departamento_id ON empleados(departamento_id);
CREATE INDEX IF NOT EXISTS ix_empleados_puesto_id ON empleados(puesto_id);
