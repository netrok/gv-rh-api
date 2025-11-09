-- V20251108_20__idx_empleados_listado.sql
CREATE INDEX IF NOT EXISTS ix_emp_num_empleado   ON empleados (num_empleado);
CREATE INDEX IF NOT EXISTS ix_emp_nombres        ON empleados (lower(nombres));
CREATE INDEX IF NOT EXISTS ix_emp_ap_paterno     ON empleados (lower(apellido_paterno));
CREATE INDEX IF NOT EXISTS ix_emp_ap_materno     ON empleados (lower(apellido_materno));
CREATE INDEX IF NOT EXISTS ix_emp_email          ON empleados (lower(email));
CREATE INDEX IF NOT EXISTS ix_emp_dep            ON empleados (departamento_id);
CREATE INDEX IF NOT EXISTS ix_emp_pue            ON empleados (puesto_id);
CREATE INDEX IF NOT EXISTS ix_emp_activo         ON empleados (activo);
CREATE INDEX IF NOT EXISTS ix_emp_fecha_ingreso  ON empleados (fecha_ingreso);