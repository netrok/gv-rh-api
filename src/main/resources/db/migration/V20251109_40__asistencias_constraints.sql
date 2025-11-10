-- V20251109_40__asistencias_constraints.sql
DO $$
DECLARE
  t        text; -- nombre real de la tabla de asistencias
  con_name text;
  idx_name text;
BEGIN
  -- Detecta el nombre real de la tabla (ajusta aquí si usas otro prefijo)
  SELECT name INTO t
  FROM (
    SELECT 'asistencias' AS name
    WHERE EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema='public' AND table_name='asistencias'
    )
    UNION ALL
    SELECT 'asist_asistencias' AS name
    WHERE EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema='public' AND table_name='asist_asistencias'
    )
  ) s
  LIMIT 1;

  IF t IS NULL THEN
    RAISE EXCEPTION 'No se encontró la tabla de asistencias (ni "asistencias" ni "asist_asistencias"). Revisa migraciones 10/11.';
  END IF;

  -- Nombre de la constraint e índice, dependientes del nombre real de la tabla
  con_name := format('uq_%s_emp_fecha', t);
  idx_name := format('ix_%s_emp_fecha', t);

  -- UNIQUE (empleado_id, fecha) si no existe la constraint
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = con_name
  ) THEN
    EXECUTE format(
      'ALTER TABLE public.%I ADD CONSTRAINT %I UNIQUE (empleado_id, fecha);',
      t, con_name
    );
  END IF;

  -- Índice en checadas si existe la tabla y no existe el índice
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema='public' AND table_name='checadas'
  ) THEN
    IF NOT EXISTS (
      SELECT 1 FROM pg_class c
      WHERE c.relname = 'ix_checadas_emp_fecha' AND c.relkind = 'i'
    ) THEN
      EXECUTE 'CREATE INDEX ix_checadas_emp_fecha ON public.checadas (empleado_id, fecha_hora);';
    END IF;
  END IF;

  -- Índice (empleado_id, fecha) en la tabla de asistencias si no existe
  IF NOT EXISTS (
    SELECT 1 FROM pg_class c
    WHERE c.relname = idx_name AND c.relkind = 'i'
  ) THEN
    EXECUTE format(
      'CREATE INDEX %I ON public.%I (empleado_id, fecha);',
      idx_name, t
    );
  END IF;
END $$;
