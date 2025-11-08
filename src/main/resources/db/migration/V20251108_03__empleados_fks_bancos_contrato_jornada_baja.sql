-- Asegura columnas en empleados
ALTER TABLE empleados
    ADD COLUMN IF NOT EXISTS banco_id          BIGINT,
    ADD COLUMN IF NOT EXISTS tipo_contrato_id  BIGINT,
    ADD COLUMN IF NOT EXISTS tipo_jornada_id   BIGINT,
    ADD COLUMN IF NOT EXISTS motivo_baja_id    BIGINT;

-- Crea índices (opcional pero recomendable)
CREATE INDEX IF NOT EXISTS ix_emp_banco_id         ON empleados (banco_id);
CREATE INDEX IF NOT EXISTS ix_emp_tipo_contrato_id ON empleados (tipo_contrato_id);
CREATE INDEX IF NOT EXISTS ix_emp_tipo_jornada_id  ON empleados (tipo_jornada_id);
CREATE INDEX IF NOT EXISTS ix_emp_motivo_baja_id   ON empleados (motivo_baja_id);

-- Añade FKs solo si no existen
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_banco'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_banco
        FOREIGN KEY (banco_id) REFERENCES cat_bancos(id) ON DELETE SET NULL;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_tipo_contrato'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_tipo_contrato
        FOREIGN KEY (tipo_contrato_id) REFERENCES cat_tipos_contrato(id) ON DELETE SET NULL;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_tipo_jornada'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_tipo_jornada
        FOREIGN KEY (tipo_jornada_id) REFERENCES cat_tipos_jornada(id) ON DELETE SET NULL;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_motivo_baja'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_motivo_baja
        FOREIGN KEY (motivo_baja_id) REFERENCES cat_motivos_baja(id) ON DELETE SET NULL;
END IF;
END$$;