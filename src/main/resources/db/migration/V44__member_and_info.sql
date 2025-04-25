ALTER TABLE scout RENAME old_scout;
ALTER TABLE old_scout
    DROP CONSTRAINT FK_SCOUT_ON_GROUP;


CREATE TABLE identification_document
(
    id      INT AUTO_INCREMENT NOT NULL,
    id_type VARCHAR(255)       NOT NULL,
    number  VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_identificationdocument PRIMARY KEY (id)
);

CREATE TABLE member
(
    id           INT AUTO_INCREMENT NOT NULL,
    type         VARCHAR(255)       NOT NULL,
    observations TEXT               NULL,
    CONSTRAINT pk_member PRIMARY KEY (id)
);

CREATE TABLE member_role_info
(
    id           INT AUTO_INCREMENT NOT NULL,
    `role`       VARCHAR(255)       NULL,
    member_id    INT                NOT NULL,
    date         date               NULL,
    role_census  INT                NULL,
    reason       VARCHAR(255)       NULL,
    observations VARCHAR(255)       NULL,
    CONSTRAINT pk_memberroleinfo PRIMARY KEY (id),
    CONSTRAINT uc_member_role_info_role_role_census UNIQUE (role, role_census),
    CONSTRAINT FK_MEMBERROLEINFO_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE personal_data
(
    id_document_id INT  NULL,
    observations   TEXT NULL,
    member_id      INT  NOT NULL,
    CONSTRAINT pk_personaldata PRIMARY KEY (member_id),
    CONSTRAINT FK_PERSONALDATA_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT FK_PERSONALDATA_ON_IDDOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT UC_PERSONALDATA_IDDOCUMENT UNIQUE (id_document_id)
);

CREATE TABLE member_file
(
    id          INT AUTO_INCREMENT NOT NULL,
    uuid        VARCHAR(255)       NOT NULL,
    name        VARCHAR(255)       NOT NULL,
    mime_type   VARCHAR(255)       NOT NULL,
    custom_name VARCHAR(255)       NULL,
    upload_date DATETIME           NOT NULL,
    CONSTRAINT pk_memberfile PRIMARY KEY (id)
);

CREATE TABLE member_extra_files
(
    member_id      INT NOT NULL,
    extra_files_id INT NOT NULL,
    CONSTRAINT fk_memfil_on_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_memfil_on_member_file FOREIGN KEY (extra_files_id) REFERENCES member_file (id)
);

CREATE TABLE member_images
(
    member_id INT NOT NULL,
    images_id INT NOT NULL,
    CONSTRAINT fk_memima_on_member FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT fk_memima_on_member_file FOREIGN KEY (images_id) REFERENCES member_file (id)
);

CREATE TABLE personal_data_documents
(
    personal_data_member_id INT NOT NULL,
    documents_id            INT NOT NULL,
    CONSTRAINT fk_perdatdoc_on_member_file FOREIGN KEY (documents_id) REFERENCES member_file (id),
    CONSTRAINT fk_perdatdoc_on_personal_data FOREIGN KEY (personal_data_member_id) REFERENCES personal_data (member_id),
    CONSTRAINT uc_personal_data_documents_documents UNIQUE (documents_id)
);

CREATE TABLE juridical_representative
(
    id             INT AUTO_INCREMENT NOT NULL,
    id_document_id INT                NULL,
    name           VARCHAR(255)       NULL,
    surname        VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    phone          VARCHAR(255)       NULL,
    landline       VARCHAR(255)       NULL,
    CONSTRAINT pk_juridicalrepresentative PRIMARY KEY (id),
    CONSTRAINT FK_JURIDICALREPRESENTATIVE_ON_IDDOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id),
    CONSTRAINT UC_JURIDICALREPRESENTATIVE_IDDOCUMENT UNIQUE (id_document_id)
);

CREATE TABLE juridical_personal_data
(
    member_id         INT          NOT NULL,
    company_name      VARCHAR(255) NULL,
    representative_id INT          NULL,
    CONSTRAINT pk_juridicalpersonaldata PRIMARY KEY (member_id),
    CONSTRAINT FK_JURIDICALPERSONALDATA_ON_ID FOREIGN KEY (member_id) REFERENCES personal_data (member_id),
    CONSTRAINT FK_JURIDICALPERSONALDATA_ON_REPRESENTATIVE FOREIGN KEY (representative_id) REFERENCES juridical_representative (id)
);

CREATE TABLE real_personal_data
(
    member_id              INT          NOT NULL,
    surname                VARCHAR(255) NOT NULL,
    name                   VARCHAR(255) NOT NULL,
    felt_name              VARCHAR(255) NULL,
    birthday               date         NULL,
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
    gender                 VARCHAR(255) NULL,
    CONSTRAINT pk_realpersonaldata PRIMARY KEY (member_id),
    CONSTRAINT FK_REALPERSONALDATA_ON_ID FOREIGN KEY (member_id) REFERENCES personal_data (member_id)
);

CREATE TABLE scout
(
    id                  INT           NOT NULL,
    scout_type          VARCHAR(255)  NULL,
    active              BIT(1)        NOT NULL,
    federated           BIT(1)        NOT NULL,
    group_id            INT           NULL,
    medical_data_old    VARCHAR(1024) NULL,
    observations_old    TEXT          NULL,
    progressions_old    TEXT          NULL,
    image_authorization BIT(1)        NOT NULL,
    census              INT           NULL,
    photo_id            INT           NULL,
    CONSTRAINT pk_scout PRIMARY KEY (id),
    CONSTRAINT FK_SCOUT_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id),
    CONSTRAINT FK_SCOUT_ON_ID FOREIGN KEY (id) REFERENCES member (id),
    CONSTRAINT FK_SCOUT_ON_PHOTO FOREIGN KEY (photo_id) REFERENCES member_file (id)
);

CREATE TABLE scout_registration_dates
(
    id                  INT AUTO_INCREMENT NOT NULL,
    registration_date   date               NULL,
    unregistration_date date               NULL,
    scout_id            INT                NOT NULL,
    CONSTRAINT pk_scoutregistrationdates PRIMARY KEY (id),
    CONSTRAINT FK_SCOUT_REGISTRATION_DATE_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);


INSERT INTO member (id, type)
SELECT id, 'REAL'
FROM old_scout;

ALTER TABLE identification_document
    ADD temp_scout_id INT;

INSERT INTO identification_document (number, id_type, temp_scout_id)
SELECT dni, 'DNI', id
FROM old_scout
WHERE dni IS NOT NULL;


INSERT INTO personal_data (member_id)
SELECT id
FROM old_scout;

UPDATE personal_data
    JOIN identification_document on personal_data.member_id = identification_document.temp_scout_id
SET personal_data.id_document_id = identification_document.id;

ALTER TABLE identification_document
    DROP temp_scout_id;

INSERT INTO real_personal_data (member_id, surname, name, birthday, gender, shirt_size, residence_municipality)
SELECT id, surname, name, birthday, gender, shirt_size, municipality
FROM old_scout;

INSERT INTO scout (id, scout_type, active, federated, group_id, medical_data_old, image_authorization, census,
                   progressions_old, observations_old)
SELECT id,
       'PARTICIPANT',
       enabled,
       enabled,
       group_id,
       medical_data,
       image_authorization,
       census,
       progressions,
       observations
FROM old_scout;

DROP TABLE old_scout;

ALTER TABLE confirmation
    ADD CONSTRAINT FK_CONFIRMATION_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id);

ALTER TABLE confirmation
    ADD CONSTRAINT FK_CONFIRMATION_ON_EVENT FOREIGN KEY (event_id) REFERENCES event (id);

ALTER TABLE scout_user
    ADD CONSTRAINT FK_SCOUTUSER_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id);

ALTER TABLE scout_user
    ADD CONSTRAINT FK_SCOUTUSER_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);