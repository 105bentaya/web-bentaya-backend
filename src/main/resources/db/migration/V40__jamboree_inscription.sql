CREATE TABLE jamboree_inscription
(
    id                INT AUTO_INCREMENT NOT NULL,
    participant_type  VARCHAR(255)       NULL,
    census            VARCHAR(255)       NULL,
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
    municipality      VARCHAR(255)       NULL,
    blood_type        VARCHAR(255)       NULL,
    medical_data      VARCHAR(2000)      NULL,
    medication        VARCHAR(2000)      NULL,
    allergies         VARCHAR(2000)      NULL,
    vaccine_program   BIT(1)             NOT NULL,
    size              VARCHAR(255)       NULL,
    food_intolerances VARCHAR(2000)      NULL,
    diet_preference   VARCHAR(2000)      NULL,
    observations      VARCHAR(2000)      NULL,
    CONSTRAINT pk_jamboreeinscription PRIMARY KEY (id)
);

CREATE TABLE jamboree_contact
(
    id             INT AUTO_INCREMENT NOT NULL,
    surname        VARCHAR(255)       NULL,
    name           VARCHAR(255)       NULL,
    mobile_phone   VARCHAR(255)       NULL,
    landline_phone VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    address        VARCHAR(511)       NULL,
    cp             VARCHAR(255)       NULL,
    locality       VARCHAR(255)       NULL,
    inscription_id INT                NOT NULL,
    CONSTRAINT pk_jamboreecontact PRIMARY KEY (id),
    CONSTRAINT FK_JAMBOREECONTACT_ON_INSCRIPTION FOREIGN KEY (inscription_id) REFERENCES jamboree_inscription (id)
);

CREATE TABLE jamboree_language
(
    id             INT AUTO_INCREMENT NOT NULL,
    language       VARCHAR(255)       NULL,
    level          VARCHAR(255)       NULL,
    inscription_id INT                NOT NULL,
    CONSTRAINT pk_jamboreelanguage PRIMARY KEY (id),
    CONSTRAINT FK_JAMBOREELANGUAGE_ON_INSCRIPTION FOREIGN KEY (inscription_id) REFERENCES jamboree_inscription (id)
);
