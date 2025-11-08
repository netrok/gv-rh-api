-- Departamentos
INSERT INTO cat_departamentos (nombre) VALUES
                                           ('Administración'),
                                           ('Sistemas')
    ON CONFLICT (nombre) DO NOTHING;

-- Puestos (ligado a 'Sistemas')
INSERT INTO cat_puestos (nombre, departamento_id) VALUES
    ('Gerente de Sistemas', (SELECT id FROM cat_departamentos WHERE nombre='Sistemas'))
    ON CONFLICT DO NOTHING;

-- Turnos base
INSERT INTO cat_turnos (nombre, hora_entrada, hora_salida, tolerancia_entrada_min, tolerancia_salida_min, ventana_inicio_min, ventana_fin_min) VALUES
    ('L-V 9-18', '09:00', '18:00', 10, 10, 180, 180)
    ON CONFLICT DO NOTHING;

-- Horarios base
INSERT INTO cat_horarios (nombre, hora_entrada, hora_salida, minutos_comida, lunes, martes, miercoles, jueves, viernes, sabado, domingo) VALUES
    ('Oficina L-V', '09:00', '18:00', 60, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, FALSE)
    ON CONFLICT DO NOTHING;