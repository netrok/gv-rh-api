-- Asegura columnas (si aún no existen)
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS tipo_contrato_id BIGINT;
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS tipo_jornada_id BIGINT;
ALTER TABLE empleados ADD COLUMN IF NOT EXISTS motivo_baja_id BIGINT;

-- FK: tipo_contrato
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_tipo_contrato'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_tipo_contrato
        FOREIGN KEY (tipo_contrato_id) REFERENCES cat_tipos_contrato(id) ON DELETE SET NULL;
END IF;
END $$;

-- FK: tipo_jornada
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_tipo_jornada'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_tipo_jornada
        FOREIGN KEY (tipo_jornada_id) REFERENCES cat_tipos_jornada(id) ON DELETE SET NULL;
END IF;
END $$;

-- FK: motivo_baja
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_motivo_baja'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_motivo_baja
        FOREIGN KEY (motivo_baja_id) REFERENCES cat_motivos_baja(id) ON DELETE SET NULL;
END IF;
END $$;