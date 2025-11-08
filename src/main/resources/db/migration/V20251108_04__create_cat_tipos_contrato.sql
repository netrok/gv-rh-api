CREATE TABLE IF NOT EXISTS cat_tipos_contrato (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  nombre VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );