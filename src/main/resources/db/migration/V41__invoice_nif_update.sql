UPDATE invoice
SET nif = UPPER(REGEXP_REPLACE(nif, '[^a-zA-Z0-9?]', ''))