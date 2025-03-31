### ADD BOOKING_DOCUMENT_FILE ENTITY

CREATE TABLE booking_document_file
(
    id        INT AUTO_INCREMENT NOT NULL,
    file_uuid VARCHAR(255)       NOT NULL,
    file_name VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_bookingdocumentfile PRIMARY KEY (id)
);

## MOVE OLD VALUES TO NEW ENTITY

INSERT INTO booking_document_file(id, file_uuid, file_name)
SELECT id, file_name, file_uuid
FROM booking_document
ORDER BY id;

ALTER TABLE booking_document
    ADD file_id INT NULL;

UPDATE booking_document bd
SET bd.file_id = bd.id;

ALTER TABLE booking_document
    MODIFY file_id INT NOT NULL;

ALTER TABLE booking_document
    ADD CONSTRAINT FK_BOOKINGDOCUMENT_ON_FILE FOREIGN KEY (file_id) REFERENCES booking_document_file (id);

## DROP OLD COLUMNS

ALTER TABLE booking_document
    DROP COLUMN file_name;

ALTER TABLE booking_document
    DROP COLUMN file_uuid;

### ADD BOOKING_DOCUMENT_TYPE ENTITY

CREATE TABLE booking_document_type
(
    id          INT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255)       NOT NULL,
    description VARCHAR(500)       NOT NULL,
    CONSTRAINT pk_bookingdocumenttype PRIMARY KEY (id)
);

CREATE TABLE scout_center_allowed_documents
(
    scout_center_id      INT NOT NULL,
    allowed_documents_id INT NOT NULL
);

ALTER TABLE scout_center_allowed_documents
    ADD CONSTRAINT fk_scocenalldoc_on_booking_document_type FOREIGN KEY (allowed_documents_id) REFERENCES booking_document_type (id);

ALTER TABLE scout_center_allowed_documents
    ADD CONSTRAINT fk_scocenalldoc_on_scout_center FOREIGN KEY (scout_center_id) REFERENCES scout_center (id);

ALTER TABLE scout_center_allowed_documents
    ADD CONSTRAINT uc_scocenalldoc UNIQUE (scout_center_id, allowed_documents_id);

## ADD LEGACY TYPE FOR EXISTING DOCUMENTS

ALTER TABLE booking_document
    ADD type_id INT NULL;

INSERT INTO booking_document_type
VALUES (1, 'Antiguos', 'Documentos antiguos');

UPDATE booking_document bd
SET bd.type_id = 1;

ALTER TABLE booking_document
    MODIFY type_id INT NOT NULL;

ALTER TABLE booking_document
    ADD CONSTRAINT FK_BOOKINGDOCUMENT_ON_TYPE FOREIGN KEY (type_id) REFERENCES booking_document_type (id);

### NEW BOOKING_DOCUMENT FIELDS

ALTER TABLE booking_document
    ADD duration VARCHAR(255) NOT NULL DEFAULT 'SINGLE_USE';

ALTER TABLE booking_document
    ALTER duration DROP DEFAULT;

ALTER TABLE booking_document
    ADD expiration_date DATE NULL;

ALTER TABLE booking_document
    ADD CONSTRAINT FK_BOOKINGDOCUMENT_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES booking (id);

### UPDATE BOOKING FIELDS

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE booking
    MODIFY exclusive_reservation BIT(1) DEFAULT 0 NOT NULL;

ALTER TABLE booking
    MODIFY own_booking BIT(1) DEFAULT 0 NOT NULL;

ALTER TABLE booking
    MODIFY packs INT NOT NULL;
