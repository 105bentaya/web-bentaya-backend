ALTER TABLE booking
    ADD finished BIT(1) DEFAULT 0 NOT NULL;

UPDATE booking
SET finished = 1
WHERE status = 'OCCUPIED'