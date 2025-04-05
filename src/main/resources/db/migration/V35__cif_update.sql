UPDATE booking
SET cif = UPPER(REGEXP_REPLACE(cif, '[^a-zA-Z0-9]', ''))
WHERE cif IS NOT NULL;


ALTER TABLE booking_document_file
    ADD user_id INT NULL;

UPDATE booking_document_file bdf
    JOIN booking_document bd ON bd.file_id = bdf.id
    JOIN booking b ON b.id = bd.booking_id
SET bdf.user_id = b.user_id;

ALTER TABLE booking_document_file
    MODIFY user_id INT NOT NULL;

ALTER TABLE booking_document_file
    ADD CONSTRAINT FK_BOOKINGDOCUMENTFILE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);