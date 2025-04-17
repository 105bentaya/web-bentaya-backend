ALTER TABLE booking_document_type
    ADD usual_duration VARCHAR(255) NULL;

UPDATE booking_document_type
SET usual_duration = 'PERMANENT'
WHERE id = 3;

UPDATE booking_document_type
SET usual_duration = 'EXPIRABLE'
WHERE id IN (2, 4, 5);

UPDATE booking_document_type
SET usual_duration = 'SINGLE_USE'
WHERE usual_duration IS NULL;

ALTER TABLE booking_document_type
    MODIFY usual_duration VARCHAR(255) NOT NULL;
