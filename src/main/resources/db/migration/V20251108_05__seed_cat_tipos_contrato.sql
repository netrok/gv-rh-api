INSERT INTO cat_tipos_contrato (nombre) VALUES
                                            ('Indeterminado'),
                                            ('Determinado'),
                                            ('Obra o proyecto'),
                                            ('Periodo de prueba'),
                                            ('Capacitación inicial')
    ON CONFLICT (nombre) DO NOTHING;