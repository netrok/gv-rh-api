CREATE TABLE IF NOT EXISTS cat_motivos_baja (
                                                id BIGSERIAL PRIMARY KEY,
                                                nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );