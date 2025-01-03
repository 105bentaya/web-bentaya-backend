ALTER TABLE pre_scout
    ADD priority_as_text VARCHAR(255);

ALTER TABLE pre_scout
    ADD assigned BOOLEAN DEFAULT FALSE;

UPDATE pre_scout
SET priority_as_text = '1. Tiene hermanos/as o es hija de scouters que están en el grupo en la Ronda Solar 2024/25'
WHERE priority = 1;

UPDATE pre_scout
SET priority_as_text = '2. Es hija de scouters o scouts que hayan pertenecido al grupo, a SEC o a ASDE'
WHERE priority = 2;

UPDATE pre_scout
SET priority_as_text = '3. Tiene hermanos o hermanas en la lista de espera para la misma ronda'
WHERE priority = 3;

UPDATE pre_scout
SET priority_as_text = 'Ninguno'
WHERE priority = 4;

ALTER TABLE pre_scout
    ADD temp_date DATETIME;

UPDATE pre_scout
SET temp_date = STR_TO_DATE(creation_date, '%d/%m/%Y %H:%i:%s');

ALTER TABLE pre_scout
    DROP COLUMN creation_date;

ALTER TABLE pre_scout
    CHANGE COLUMN temp_date creation_date DATETIME;

ALTER TABLE pre_scout MODIFY inscription_year INT;
