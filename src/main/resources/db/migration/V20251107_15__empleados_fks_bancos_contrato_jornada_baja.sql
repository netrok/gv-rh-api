DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='fk_emp_banco'
  ) THEN
    ALTER TABLE empleados
      ADD CONSTRAINT fk_emp_banco
      FOREIGN KEY (banco_id) REFERENCES cat_bancos(id) ON DELETE SET NULL;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='fk_emp_tipo_contrato'
  ) THEN
    ALTER TABLE empleados
      ADD CONSTRAINT fk_emp_tipo_contrato
      FOREIGN KEY (tipo_contrato_id) REFERENCES cat_tipo_contrato(id) ON DELETE SET NULL;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='fk_emp_tipo_jornada'
  ) THEN
    ALTER TABLE empleados
      ADD CONSTRAINT fk_emp_tipo_jornada
      FOREIGN KEY (tipo_jornada_id) REFERENCES cat_tipo_jornada(id) ON DELETE SET NULL;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='fk_emp_motivo_baja'
  ) THEN
    ALTER TABLE empleados
      ADD CONSTRAINT fk_emp_motivo_baja
      FOREIGN KEY (motivo_baja_id) REFERENCES cat_motivo_baja(id) ON DELETE SET NULL;
  END IF;
END$$;

CREATE INDEX IF NOT EXISTS ix_emp_banco        ON empleados (banco_id);
CREATE INDEX IF NOT EXISTS ix_emp_tipo_contrato ON empleados (tipo_contrato_id);
CREATE INDEX IF NOT EXISTS ix_emp_tipo_jornada  ON empleados (tipo_jornada_id);
CREATE INDEX IF NOT EXISTS ix_emp_motivo_baja   ON empleados (motivo_baja_id);
