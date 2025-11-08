INSERT INTO cat_motivos_baja (nombre) VALUES
                                          ('Renuncia voluntaria'),
                                          ('Despido'),
                                          ('Fin de contrato'),
                                          ('Jubilación'),
                                          ('Defunción')
    ON CONFLICT (nombre) DO NOTHING;