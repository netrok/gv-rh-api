CREATE TABLE IF NOT EXISTS empleado (
                                        id BIGSERIAL PRIMARY KEY,
                                        num_empleado VARCHAR(30) NOT NULL UNIQUE,
    nombres VARCHAR(120) NOT NULL,
    apellido_paterno VARCHAR(120) NOT NULL,
    apellido_materno VARCHAR(120),
    email VARCHAR(160),
    activo BOOLEAN DEFAULT TRUE
    );
