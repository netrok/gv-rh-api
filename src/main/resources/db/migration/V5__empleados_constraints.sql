-- Unicidades duras en identificadores
ALTER TABLE empleados
    ADD CONSTRAINT uq_empleados_curp UNIQUE (curp);

ALTER TABLE empleados
    ADD CONSTRAINT uq_empleados_rfc UNIQUE (rfc);

ALTER TABLE empleados
    ADD CONSTRAINT uq_empleados_nss UNIQUE (nss);

-- (Opcional) En muchos sistemas también se hace único:
-- ALTER TABLE empleados ADD CONSTRAINT uq_empleados_num_empleado UNIQUE (num_empleado);
-- ALTER TABLE empleados ADD CONSTRAINT uq_empleados_email UNIQUE (email);

-- Índices para búsqueda/filtros frecuentes
CREATE INDEX IF NOT EXISTS ix_empleados_num_empleado   ON empleados (num_empleado);
CREATE INDEX IF NOT EXISTS ix_empleados_activo         ON empleados (activo);
CREATE INDEX IF NOT EXISTS ix_empleados_apellido_pat   ON empleados (apellido_paterno);
CREATE INDEX IF NOT EXISTS ix_empleados_apellido_mat   ON empleados (apellido_materno);
CREATE INDEX IF NOT EXISTS ix_empleados_nombres        ON empleados (nombres);
CREATE INDEX IF NOT EXISTS ix_empleados_fecha_ingreso  ON empleados (fecha_ingreso);

-- Si sueles buscar por CURP/RFC/NSS (además del UNIQUE):
CREATE INDEX IF NOT EXISTS ix_empleados_curp ON empleados (curp);
CREATE INDEX IF NOT EXISTS ix_empleados_rfc  ON empleados (rfc);
CREATE INDEX IF NOT EXISTS ix_empleados_nss  ON empleados (nss);
