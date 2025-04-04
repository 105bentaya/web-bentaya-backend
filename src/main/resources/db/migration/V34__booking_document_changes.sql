### ADD BOOKING_DOCUMENT_FILE ENTITY

CREATE TABLE booking_document_file
(
    id        INT AUTO_INCREMENT NOT NULL,
    uuid      VARCHAR(255)       NOT NULL,
    name      VARCHAR(255)       NOT NULL,
    mime_type VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_bookingdocumentfile PRIMARY KEY (id)
);

## MOVE OLD VALUES TO NEW ENTITY

INSERT INTO booking_document_file(id, uuid, name, mime_type)
SELECT id, file_uuid, file_name, 'application/pdf'
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

ALTER TABLE booking_document
    ADD observations VARCHAR(511) NULL;

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
    active      BIT(1) DEFAULT 1   NOT NULL,
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
VALUES (1, 'Antiguos', 'Documentos antiguos', 0),
       (2, 'Registro', 'Registro de Asociaciones o registro acreditativo del Centro Educativo, según corresponda', 1),
       (3, 'Estatutos', 'Copia de los estatutos o documento acreditativo del Centro Educativo, según corresponda', 1),
       (4, 'Seguro de responsabilidad civil', 'Recibo vigente de seguro de responsabilidad civil, si así corresponde',
        1),
       (5, 'Seguro de accidentes', 'Recibo vigente del seguro de accidentes, si así corresponde', 1),
       (6, 'Comprobante del ingreso',
        'Comprobante del ingreso, a nuestro favor, de la aportación por la cesión de uso en nuestra cuenta corriente (ES29 2100 1675 3402 0039 5888)',
        1),
       (7, 'Listado de asistentes',
        'Listado de todos los asistentes (emplee la plantilla aportada), especificando la función en el caso de los monitores o responsables',
        1),
       (8, 'Otros',
        'Otros documentos que consideren necesario aportar',
        1);

UPDATE booking_document bd
SET bd.type_id = 1;

ALTER TABLE booking_document
    MODIFY type_id INT NOT NULL;

ALTER TABLE booking_document
    ADD CONSTRAINT FK_BOOKINGDOCUMENT_ON_TYPE FOREIGN KEY (type_id) REFERENCES booking_document_type (id);

### NEW BOOKING_DOCUMENT FIELDS

ALTER TABLE booking_document
    ADD duration VARCHAR(255) NULL;

UPDATE booking_document bd
SET bd.duration = 'SINGLE_USE';

ALTER TABLE booking_document
    ADD expiration_date DATE NULL;

ALTER TABLE booking_document
    ADD CONSTRAINT FK_BOOKINGDOCUMENT_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES booking (id);

### UPDATE BOOKING FIELDS

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE booking
    ADD incidences_file_id INT NULL;

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_INCIDENCES_FILE FOREIGN KEY (incidences_file_id) REFERENCES booking_document_file (id);

ALTER TABLE booking
    MODIFY exclusive_reservation BIT(1) DEFAULT 0 NOT NULL;

ALTER TABLE booking
    MODIFY own_booking BIT(1) DEFAULT 0 NOT NULL;

ALTER TABLE booking
    MODIFY packs INT NOT NULL;
