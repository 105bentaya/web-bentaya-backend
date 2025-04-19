DROP TABLE IF EXISTS jamboree_contact;
DROP TABLE IF EXISTS jamboree_language;
DROP TABLE IF EXISTS jamboree_inscription;

CREATE TABLE jamboree_contact
(
    id             INT AUTO_INCREMENT NOT NULL,
    surname        VARCHAR(255)       NULL,
    name           VARCHAR(255)       NULL,
    mobile_phone   VARCHAR(255)       NULL,
    landline_phone VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    inscription_id INT                NOT NULL,
    CONSTRAINT pk_jamboreecontact PRIMARY KEY (id)
);

CREATE TABLE jamboree_inscription
(
    id                INT AUTO_INCREMENT NOT NULL,
    census            VARCHAR(255)       NULL,
    participant_type  VARCHAR(255)       NULL,
    surname           VARCHAR(255)       NULL,
    name              VARCHAR(255)       NULL,
    felt_name         VARCHAR(255)       NULL,
    dni               VARCHAR(255)       NULL,
    passport_number   VARCHAR(255)       NULL,
    nationality       VARCHAR(255)       NULL,
    birth_date        date               NULL,
    age_at_jamboree   VARCHAR(255)       NULL,
    gender            VARCHAR(255)       NULL,
    phone_number      VARCHAR(255)       NULL,
    email             VARCHAR(255)       NULL,
    resident          BIT(1)             NOT NULL,
    municipality      VARCHAR(511)       NULL,
    address           VARCHAR(511)       NULL,
    cp                VARCHAR(255)       NULL,
    locality          VARCHAR(255)       NULL,
    blood_type        VARCHAR(255)       NULL,
    medical_data      TEXT               NULL,
    medication        TEXT               NULL,
    allergies         TEXT               NULL,
    vaccine_program   BIT(1)             NOT NULL,
    size              TEXT               NULL,
    food_intolerances TEXT               NULL,
    diet_preference   TEXT               NULL,
    observations      TEXT               NULL,
    CONSTRAINT pk_jamboreeinscription PRIMARY KEY (id)
);

CREATE TABLE jamboree_language
(
    id             INT AUTO_INCREMENT NOT NULL,
    language       VARCHAR(255)       NULL,
    level          VARCHAR(255)       NULL,
    inscription_id INT                NOT NULL,
    CONSTRAINT pk_jamboreelanguage PRIMARY KEY (id)
);

ALTER TABLE jamboree_contact
    ADD CONSTRAINT FK_JAMBOREECONTACT_ON_INSCRIPTION FOREIGN KEY (inscription_id) REFERENCES jamboree_inscription (id);

ALTER TABLE jamboree_language
    ADD CONSTRAINT FK_JAMBOREELANGUAGE_ON_INSCRIPTION FOREIGN KEY (inscription_id) REFERENCES jamboree_inscription (id);