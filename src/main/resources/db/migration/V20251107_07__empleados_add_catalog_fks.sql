DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_departamento'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_departamento
        FOREIGN KEY (departamento_id) REFERENCES cat_departamentos(id) ON DELETE SET NULL;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_puesto'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_puesto
        FOREIGN KEY (puesto_id) REFERENCES cat_puestos(id) ON DELETE SET NULL;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_turno'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_turno
        FOREIGN KEY (turno_id) REFERENCES cat_turnos(id) ON DELETE SET NULL;
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_emp_horario'
  ) THEN
ALTER TABLE empleados
    ADD CONSTRAINT fk_emp_horario
        FOREIGN KEY (horario_id) REFERENCES cat_horarios(id) ON DELETE SET NULL;
END IF;
END$$;

-- Índices por si faltan
CREATE INDEX IF NOT EXISTS ix_emp_dep  ON empleados (departamento_id);
CREATE INDEX IF NOT EXISTS ix_emp_pue  ON empleados (puesto_id);
CREATE INDEX IF NOT EXISTS ix_emp_tur  ON empleados (turno_id);
CREATE INDEX IF NOT EXISTS ix_emp_hor  ON empleados (horario_id);