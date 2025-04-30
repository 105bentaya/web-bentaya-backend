ALTER TABLE bentaya_group
    ADD COLUMN section VARCHAR(255) NULL;

UPDATE bentaya_group
SET section = 'CASTORES'
WHERE id = 1;

UPDATE bentaya_group
SET section = 'LOBATOS'
WHERE id = 2
   OR id = 3;

UPDATE bentaya_group
SET section = 'SCOUTS'
WHERE id = 4
   OR id = 5;

UPDATE bentaya_group
SET section = 'ESCULTAS'
WHERE id = 6;

UPDATE bentaya_group
SET section = 'ROVERS'
WHERE id = 7;

ALTER TABLE bentaya_group
    MODIFY section VARCHAR(255) NOT NULL;