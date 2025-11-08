-- Bancos / Tipos de contrato / Tipos de jornada / Motivos de baja
CREATE TABLE IF NOT EXISTS cat_bancos (
                                          id BIGSERIAL PRIMARY KEY,
                                          nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS cat_tipos_contrato (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS cat_tipos_jornada (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS cat_motivos_baja (
                                                id BIGSERIAL PRIMARY KEY,
                                                nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

-- Seeds mínimos
INSERT INTO cat_bancos (nombre) VALUES
                                    ('BBVA'), ('Citibanamex'), ('Santander'), ('Banorte')
    ON CONFLICT DO NOTHING;

INSERT INTO cat_tipos_contrato (nombre) VALUES
                                            ('Indefinido'), ('Temporal'), ('Por proyecto')
    ON CONFLICT DO NOTHING;

INSERT INTO cat_tipos_jornada (nombre) VALUES
                                           ('Tiempo completo'), ('Medio tiempo'), ('Mixta')
    ON CONFLICT DO NOTHING;

INSERT INTO cat_motivos_baja (nombre) VALUES
                                          ('Renuncia'), ('Despido'), ('Término de contrato'), ('Abandono')
    ON CONFLICT DO NOTHING;