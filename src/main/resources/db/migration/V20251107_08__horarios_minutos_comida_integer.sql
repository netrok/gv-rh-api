-- Normaliza el tipo para que coincida con la Entity (Integer)
ALTER TABLE cat_horarios
  ALTER COLUMN minutos_comida TYPE integer USING minutos_comida::integer;
