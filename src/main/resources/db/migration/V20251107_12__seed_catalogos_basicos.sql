-- Semillas idempotentes
INSERT INTO cat_bancos (nombre) VALUES
                                    ('BBVA'), ('Banamex'), ('Santander'), ('HSBC'), ('Banorte')
    ON CONFLICT (nombre) DO NOTHING;

INSERT INTO cat_tipos_contrato (nombre) VALUES
                                            ('Indeterminado'), ('Temporal'), ('Por Proyecto'), ('Honorarios')
    ON CONFLICT (nombre) DO NOTHING;

INSERT INTO cat_tipos_jornada (nombre) VALUES
                                           ('Tiempo Completo'), ('Medio Tiempo'), ('Por Turnos')
    ON CONFLICT (nombre) DO NOTHING;

INSERT INTO cat_motivos_baja (nombre) VALUES
                                          ('Renuncia'), ('Despido'), ('Fin de Contrato'), ('Jubilación'), ('Defunción')
    ON CONFLICT (nombre) DO NOTHING;