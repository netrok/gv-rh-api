-- Campos nuevos solicitados (todos NULL por compatibilidad)
ALTER TABLE empleados
    -- Básicos ya existentes: num_empleado, nombres, apellido_paterno, apellido_materno, email, activo, foto (si ya existe)
    ADD COLUMN IF NOT EXISTS fecha_nacimiento date,
    ADD COLUMN IF NOT EXISTS genero           varchar(20),
    ADD COLUMN IF NOT EXISTS estado_civil     varchar(30),
    ADD COLUMN IF NOT EXISTS curp             varchar(18),
    ADD COLUMN IF NOT EXISTS rfc              varchar(13),
    ADD COLUMN IF NOT EXISTS nss              varchar(15),
    ADD COLUMN IF NOT EXISTS telefono         varchar(30),
    ADD COLUMN IF NOT EXISTS fecha_ingreso    date,
    ADD COLUMN IF NOT EXISTS foto             varchar(255),

    -- Catálogos (por ahora como IDs sueltos; luego FKs)
    ADD COLUMN IF NOT EXISTS departamento_id  bigint,
    ADD COLUMN IF NOT EXISTS puesto_id        bigint,
    ADD COLUMN IF NOT EXISTS turno_id         bigint,
    ADD COLUMN IF NOT EXISTS horario_id       bigint,
    ADD COLUMN IF NOT EXISTS supervisor_id    bigint,

    -- Dirección
    ADD COLUMN IF NOT EXISTS calle            varchar(200),
    ADD COLUMN IF NOT EXISTS num_ext          varchar(20),
    ADD COLUMN IF NOT EXISTS num_int          varchar(20),
    ADD COLUMN IF NOT EXISTS colonia          varchar(120),
    ADD COLUMN IF NOT EXISTS municipio        varchar(120),
    ADD COLUMN IF NOT EXISTS estado           varchar(120),
    ADD COLUMN IF NOT EXISTS cp               varchar(10),
    ADD COLUMN IF NOT EXISTS nacionalidad     varchar(80),
    ADD COLUMN IF NOT EXISTS lugar_nacimiento varchar(120),
    ADD COLUMN IF NOT EXISTS escolaridad      varchar(80),
    ADD COLUMN IF NOT EXISTS tipo_sangre      varchar(10),

    -- Contacto de emergencia
    ADD COLUMN IF NOT EXISTS contacto_nombre      varchar(120),
    ADD COLUMN IF NOT EXISTS contacto_telefono    varchar(30),
    ADD COLUMN IF NOT EXISTS contacto_parentesco  varchar(60),

    -- Bancario
    ADD COLUMN IF NOT EXISTS banco_id         bigint,
    ADD COLUMN IF NOT EXISTS cuenta_bancaria  varchar(30),
    ADD COLUMN IF NOT EXISTS clabe            varchar(18),

    -- Contrato/Jornada/Motivo baja
    ADD COLUMN IF NOT EXISTS tipo_contrato_id bigint,
    ADD COLUMN IF NOT EXISTS tipo_jornada_id  bigint,

    -- Bajas
    ADD COLUMN IF NOT EXISTS fecha_baja       date,
    ADD COLUMN IF NOT EXISTS motivo_baja_id   bigint,

    -- Otros
    ADD COLUMN IF NOT EXISTS imss_reg_patronal varchar(20),
    ADD COLUMN IF NOT EXISTS infonavit_numero  varchar(20),
    ADD COLUMN IF NOT EXISTS fonacot_numero    varchar(20)
;
