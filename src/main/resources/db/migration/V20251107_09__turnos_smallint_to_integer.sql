ALTER TABLE cat_turnos
  ALTER COLUMN tolerancia_entrada_min TYPE integer USING tolerancia_entrada_min::integer,
  ALTER COLUMN tolerancia_salida_min  TYPE integer USING tolerancia_salida_min::integer,
  ALTER COLUMN ventana_inicio_min     TYPE integer USING ventana_inicio_min::integer,
  ALTER COLUMN ventana_fin_min        TYPE integer USING ventana_fin_min::integer;
