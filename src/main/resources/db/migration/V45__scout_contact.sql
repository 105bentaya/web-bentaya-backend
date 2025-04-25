CREATE TABLE scout_contact
(
    id             INT AUTO_INCREMENT NOT NULL,
    person_type    VARCHAR(255)       NOT NULL,
    name           VARCHAR(255)       NOT NULL,
    relationship   VARCHAR(255)       NULL,
    donor          BIT(1)             NOT NULL,
    surname        VARCHAR(255)       NULL,
    id_document_id INT                NULL,
    phone          VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    studies        VARCHAR(255)       NULL,
    profession     VARCHAR(255)       NULL,
    company_name   VARCHAR(255)       NULL,
    observations   TEXT               NULL,
    scout_id       INT                NOT NULL,
    CONSTRAINT pk_scoutcontact PRIMARY KEY (id),
    CONSTRAINT FK_SCOUTCONTACT_ON_IDDOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT FK_SCOUTCONTACT_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);

INSERT INTO scout_contact(person_type, name, relationship, donor, phone, email, scout_id)
SELECT 'REAL',
       name,
       relationship,
       false,
       phone,
       email,
       scout_id
FROM contact;

DROP TABLE contact;