ALTER TABLE scout RENAME old_scout;
ALTER TABLE old_scout
    DROP CONSTRAINT FK_SCOUT_ON_GROUP;


CREATE TABLE identification_document
(
    id      INT AUTO_INCREMENT NOT NULL,
    id_type VARCHAR(255)       NOT NULL,
    number  VARCHAR(255)       NOT NULL,
    CONSTRAINT PK_IDENTIFICATION_DOCUMENT PRIMARY KEY (id)
);

CREATE TABLE scout_file
(
    id          INT AUTO_INCREMENT NOT NULL,
    uuid        VARCHAR(255)       NOT NULL,
    name        VARCHAR(255)       NOT NULL,
    mime_type   VARCHAR(255)       NOT NULL,
    custom_name VARCHAR(255)       NULL,
    upload_date datetime           NOT NULL,
    CONSTRAINT PK_SCOUT_FILE PRIMARY KEY (id)
);



CREATE TABLE scout
(
    id                  INT AUTO_INCREMENT NOT NULL,
    observations        TEXT               NULL,
    scout_type          VARCHAR(255)       NOT NULL,
    active              BIT(1)             NOT NULL,
    federated           BIT(1)             NOT NULL,
    census              INT                NULL,
    image_authorization BIT(1)             NOT NULL,
    progressions_old    TEXT               NULL,
    observations_old    TEXT               NULL,
    group_id            INT                NULL,
    CONSTRAINT PK_SCOUT PRIMARY KEY (id),
    CONSTRAINT FK_SCOUT_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id)
);

CREATE TABLE scout_registration_dates
(
    id                  INT AUTO_INCREMENT NOT NULL,
    registration_date   date               NULL,
    unregistration_date date               NULL,
    scout_id            INT                NOT NULL,
    CONSTRAINT PK_SCOUT_REGISTRATION_DATES PRIMARY KEY (id),
    CONSTRAINT FK_SCOUT_REGISTRATION_DATE_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);


CREATE TABLE scout_extra_files
(
    scout_id       INT NOT NULL,
    extra_files_id INT NOT NULL,
    CONSTRAINT UC_SCOUT_EXTRA_FILES_EXTRA_FILES UNIQUE (extra_files_id),
    CONSTRAINT FK_SCOUT_EXTRA_FILES_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id),
    CONSTRAINT FK_SCOUT_EXTRA_FILES_ON_SCOUT_FILE FOREIGN KEY (extra_files_id) REFERENCES scout_file (id)
);

CREATE TABLE scout_images
(
    scout_id  INT NOT NULL,
    images_id INT NOT NULL,
    CONSTRAINT UC_SCOUT_IMAGES_IMAGES UNIQUE (images_id),
    CONSTRAINT FK_SCOUT_IMAGES_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id),
    CONSTRAINT FK_SCOUT_IMAGES_ON_SCOUT_FILE FOREIGN KEY (images_id) REFERENCES scout_file (id)
);

CREATE TABLE personal_data
(
    scout_id               INT          NOT NULL,
    id_document_id         INT          NULL,
    surname                VARCHAR(255) NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    felt_name              VARCHAR(255) NULL,
    birthday               date         NOT NULL,
    birthplace             VARCHAR(255) NULL,
    birth_province         VARCHAR(255) NULL,
    nationality            VARCHAR(255) NULL,
    address                VARCHAR(255) NULL,
    city                   VARCHAR(255) NULL,
    province               VARCHAR(255) NULL,
    phone                  VARCHAR(255) NULL,
    landline               VARCHAR(255) NULL,
    email                  VARCHAR(255) NULL,
    shirt_size             VARCHAR(255) NULL,
    residence_municipality VARCHAR(255) NULL,
    gender                 VARCHAR(255) NOT NULL,
    observations           TEXT         NULL,
    CONSTRAINT PK_PERSONAL_DATA PRIMARY KEY (scout_id),
    CONSTRAINT FK_PERSONAL_DATA_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id),
    CONSTRAINT FK_PERSONAL_DATA_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT UC_PERSONAL_DATA_ID_DOCUMENT UNIQUE (id_document_id)
);

CREATE TABLE personal_data_documents
(
    personal_data_scout_id INT NOT NULL,
    documents_id           INT NOT NULL,
    CONSTRAINT FK_PERSONAL_DATA_DOCUMENTS_ON_PERSONAL_DATA FOREIGN KEY (personal_data_scout_id) REFERENCES personal_data (scout_id),
    CONSTRAINT FK_PERSONAL_DATA_DOCUMENTS_ON_SCOUT_FILE FOREIGN KEY (documents_id) REFERENCES scout_file (id),
    CONSTRAINT UC_PERSONAL_DATA_DOCUMENTS_DOCUMENTS UNIQUE (documents_id)
);

CREATE TABLE scout_contact
(
    id             INT AUTO_INCREMENT NOT NULL,
    person_type    VARCHAR(255)       NOT NULL,
    name           VARCHAR(255)       NOT NULL,
    surname        VARCHAR(255)       NULL,
    relationship   VARCHAR(255)       NULL,
    donor          BIT(1)             NOT NULL,
    id_document_id INT                NULL,
    phone          VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    studies        VARCHAR(255)       NULL,
    profession     VARCHAR(255)       NULL,
    company_name   VARCHAR(255)       NULL,
    observations   TEXT               NULL,
    scout_id       INT                NOT NULL,
    CONSTRAINT PK_SCOUT_CONTACT PRIMARY KEY (id),
    CONSTRAINT FK_SCOUT_CONTACT_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT FK_SCOUT_CONTACT_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);


CREATE TABLE insurance_holder
(
    id             INT AUTO_INCREMENT NOT NULL,
    contact_id     INT                NULL,
    name           VARCHAR(255)       NULL,
    surname        VARCHAR(255)       NULL,
    id_document_id INT                NULL,
    phone          VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    CONSTRAINT PK_INSURANCE_HOLDER PRIMARY KEY (id),
    CONSTRAINT FK_INSURANCE_HOLDER_ON_CONTACT FOREIGN KEY (contact_id) REFERENCES scout_contact (id),
    CONSTRAINT FK_INSURANCE_HOLDER_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id)
);

CREATE TABLE medical_data
(
    scout_id                    INT          NOT NULL,
    blood_type                  VARCHAR(255) NOT NULL,
    social_security_number      VARCHAR(255) NULL,
    social_security_holder_id   INT          NULL,
    private_insurance_number    VARCHAR(255) NULL,
    private_insurance_entity    VARCHAR(255) NULL,
    private_insurance_holder_id INT          NULL,
    food_intolerances           TEXT         NULL,
    food_allergies              TEXT         NULL,
    food_problems               TEXT         NULL,
    food_medication             TEXT         NULL,
    food_diet                   TEXT         NULL,
    medical_intolerances        TEXT         NULL,
    medical_allergies           TEXT         NULL,
    medical_diagnoses           TEXT         NULL,
    medical_precautions         TEXT         NULL,
    medical_medications         TEXT         NULL,
    medical_emergencies         TEXT         NULL,
    addictions                  TEXT         NULL,
    tendencies                  TEXT         NULL,
    records                     TEXT         NULL,
    bullying_protocol           TEXT         NULL,
    CONSTRAINT PK_MEDICAL_DATA PRIMARY KEY (scout_id),
    CONSTRAINT FK_MEDICAL_DATA_ON_PRIVATE_INSURANCE_HOLDER FOREIGN KEY (private_insurance_holder_id) REFERENCES insurance_holder (id),
    CONSTRAINT FK_MEDICAL_DATA_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id),
    CONSTRAINT FK_MEDICAL_DATA_ON_SOCIAL_SECURITY_HOLDER FOREIGN KEY (social_security_holder_id) REFERENCES insurance_holder (id)
);

CREATE TABLE medical_data_documents
(
    medical_data_scout_id INT NOT NULL,
    documents_id          INT NOT NULL,
    CONSTRAINT UC_MEDICAL_DATA_DOCUMENTS_DOCUMENTS UNIQUE (documents_id),
    CONSTRAINT FK_MEDICAL_DATA_DOCUMENTS_ON_MEDICAL_DATA FOREIGN KEY (medical_data_scout_id) REFERENCES medical_data (scout_id),
    CONSTRAINT FK_MEDICAL_DATA_DOCUMENTS_ON_SCOUT_FILE FOREIGN KEY (documents_id) REFERENCES scout_file (id)
);

CREATE TABLE special_member
(
    id             INT AUTO_INCREMENT NOT NULL,
    `role`         VARCHAR(255)       NULL,
    role_census    INT                NOT NULL,
    agreement_date date               NULL,
    award_date     date               NULL,
    details        VARCHAR(255)       NULL,
    observations   TEXT               NULL,
    scout_id       INT                NULL,
    type           VARCHAR(255)       NOT NULL,
    name           VARCHAR(255)       NULL,
    surname        VARCHAR(255)       NULL,
    company_name   VARCHAR(255)       NULL,
    id_document_id INT                NOT NULL,
    phone          VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    CONSTRAINT PK_SPECIAL_MEMBER PRIMARY KEY (id),
    CONSTRAINT FK_SPECIAL_MEMBER_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT FK_SPECIAL_MEMBER_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);

CREATE TABLE donor
(
    id             INT AUTO_INCREMENT NOT NULL,
    role_census    INT                NOT NULL,
    scout_id       INT                NULL,
    type           VARCHAR(255)       NOT NULL,
    name           VARCHAR(255)       NULL,
    surname        VARCHAR(255)       NULL,
    company_name   VARCHAR(255)       NULL,
    id_document_id INT                NOT NULL,
    phone          VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    address        VARCHAR(511)       NOT NULL,
    CONSTRAINT PK_DONOR PRIMARY KEY (id),
    CONSTRAINT FK_DONOR_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT FK_DONOR_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);

CREATE TABLE donor_donation
(
    id       INT AUTO_INCREMENT NOT NULL,
    date     datetime           NOT NULL,
    donor_id INT                NOT NULL,
    amount   INT                NOT NULL,
    CONSTRAINT PK_DONOR_DONATION PRIMARY KEY (id),
    CONSTRAINT FK_DONOR_DONATION_ON_DONOR FOREIGN KEY (donor_id) REFERENCES donor (id)
);

INSERT INTO scout (id, scout_type, active, federated, group_id, image_authorization, census,
                   progressions_old, observations_old)
SELECT id,
       IF(enabled, 'PARTICIPANT', 'INACTIVE'),
       enabled,
       enabled,
       IF(enabled, group_id, null),
#        medical_data,
       image_authorization,
       census,
       progressions,
       observations
FROM old_scout;

INSERT INTO personal_data(scout_id, surname, name, birthday, gender, shirt_size, residence_municipality)
SELECT id, surname, name, IFNULL(birthday, NOW()), gender, shirt_size, municipality
FROM old_scout;


ALTER TABLE identification_document
    ADD temp_scout_id INT;

INSERT INTO identification_document (number, id_type, temp_scout_id)
SELECT dni, 'DNI', id
FROM old_scout
WHERE dni IS NOT NULL;

UPDATE personal_data
    JOIN identification_document on personal_data.scout_id = identification_document.temp_scout_id
SET personal_data.id_document_id = identification_document.id;

ALTER TABLE identification_document
    DROP temp_scout_id;


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

INSERT INTO medical_data(scout_id, blood_type, medical_diagnoses)
SELECT id, 'NA', medical_data
FROM old_scout;

DROP TABLE old_scout;

ALTER TABLE confirmation
    ADD CONSTRAINT FK_CONFIRMATION_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id);

ALTER TABLE confirmation
    ADD CONSTRAINT FK_CONFIRMATION_ON_EVENT FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE scout_user
    ADD CONSTRAINT FK_SCOUT_USER_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id);

ALTER TABLE scout_user
    ADD CONSTRAINT FK_SCOUT_USER_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);
