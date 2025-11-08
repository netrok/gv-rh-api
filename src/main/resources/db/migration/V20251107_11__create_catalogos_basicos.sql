-- Tablas base de catálogos
CREATE TABLE IF NOT EXISTS cat_bancos (
                                          id BIGSERIAL PRIMARY KEY,
                                          nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS cat_tipos_contrato (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS cat_tipos_jornada (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE TABLE IF NOT EXISTS cat_motivos_baja (
                                                id BIGSERIAL PRIMARY KEY,
                                                nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );