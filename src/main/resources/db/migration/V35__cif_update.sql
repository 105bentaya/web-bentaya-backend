UPDATE booking
SET cif = UPPER(REGEXP_REPLACE(cif, '[^a-zA-Z0-9]', ''))
WHERE cif IS NOT NULL;