-- Ejemplos (ajústalos a tu realidad)
INSERT INTO feriado (fecha, descripcion) VALUES
('2025-01-01','Año Nuevo'),
('2025-02-05','Constitución'),
('2025-03-21','Natalicio Benito Juárez'),
('2025-05-01','Día del Trabajo'),
('2025-09-16','Independencia'),
('2025-11-20','Revolución'),
('2025-12-25','Navidad')
ON CONFLICT (fecha) DO NOTHING;
