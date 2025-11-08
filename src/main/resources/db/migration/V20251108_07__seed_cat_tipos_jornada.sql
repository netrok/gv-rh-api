INSERT INTO cat_tipos_jornada (nombre) VALUES
                                           ('Tiempo completo'),
                                           ('Medio tiempo'),
                                           ('Por horas'),
                                           ('Nocturna'),
                                           ('Mixta')
    ON CONFLICT (nombre) DO NOTHING;