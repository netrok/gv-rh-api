-- Departamentos
INSERT INTO cat_departamentos (nombre) VALUES
('Administración') ON CONFLICT DO NOTHING,
('Sistemas') ON CONFLICT DO NOTHING;

-- Puestos
INSERT INTO cat_puestos (nombre, departamento_id)
SELECT 'Gerente de Sistemas', d.id FROM cat_departamentos d WHERE d.nombre='Sistemas'
ON CONFLICT DO NOTHING;

-- Turnos base
INSERT INTO cat_turnos (nombre, hora_entrada, hora_salida, tolerancia_entrada_min, tolerancia_salida_min, ventana_inicio_min, ventana_fin_min)
VALUES ('L-V 9-18', '09:00', '18:00', 10, 10, 180, 180)
ON CONFLICT DO NOTHING;

-- Horarios base
INSERT INTO cat_horarios (nombre, hora_entrada, hora_salida, minutos_comida, lunes, martes, miercoles, jueves, viernes, sabado, domingo)
VALUES ('Oficina L-V', '09:00', '18:00', 60, true, true, true, true, true, false, false)
ON CONFLICT DO NOTHING;
