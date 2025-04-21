UPDATE scout
SET birthday = DATE_ADD(birthday, INTERVAL 1 HOUR)
WHERE TIME(birthday) = '23:00:00';

ALTER TABLE scout
MODIFY birthday DATE NULL;