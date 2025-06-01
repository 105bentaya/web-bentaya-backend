CREATE TABLE special_member_person
(
    id             INT AUTO_INCREMENT NOT NULL,
    type           VARCHAR(255)       NOT NULL,
    name           VARCHAR(255)       NULL,
    surname        VARCHAR(255)       NULL,
    company_name   VARCHAR(255)       NULL,
    id_document_id INT                NOT NULL,
    phone          VARCHAR(255)       NULL,
    email          VARCHAR(255)       NULL,
    CONSTRAINT PK_SPECIAL_MEMBER_PERSON PRIMARY KEY (id),
    CONSTRAINT FK_SPECIAL_MEMBER_PERSON_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id)
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
    person_id      INT                NULL,
    CONSTRAINT PK_SPECIAL_MEMBER PRIMARY KEY (id),
    CONSTRAINT FK_SPECIAL_MEMBER_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id),
    CONSTRAINT FK_SPECIAL_MEMBER_ON_PERSON FOREIGN KEY (person_id) REFERENCES special_member_person (id),
    CONSTRAINT UC_SPECIAL_MEMBER_ROLE_AND_ROLE_CENSUS UNIQUE (role, role_census),
    CONSTRAINT UC_SPECIAL_MEMBER_ROLE_AND_SCOUT_ID UNIQUE (role, scout_id),
    CONSTRAINT UC_SPECIAL_MEMBER_ROLE_AND_PERSON_ID UNIQUE (role, person_id)
);

CREATE TABLE special_member_donation
(
    id                    INT AUTO_INCREMENT NOT NULL,
    date                  DATE               NOT NULL,
    type                  VARCHAR(255)       NOT NULL,
    in_kind_donation_type VARCHAR(255)       NULL,
    amount                INT                NULL,
    payment_type          VARCHAR(255)       NULL,
    bank_account          VARCHAR(255)       NULL,
    notes                 VARCHAR(511)       NULL,
    special_member_id     INT                NOT NULL,
    CONSTRAINT PK_SPECIAL_MEMBER_DONATION PRIMARY KEY (id),
    CONSTRAINT FK_SPECIAL_MEMBER_DONATION_ON_SPECIAL_MEMBER FOREIGN KEY (special_member_id) REFERENCES special_member (id)
);

INSERT INTO setting(name, value)
VALUES ('LAST_CENSUS_FOUNDER', 0),
       ('LAST_CENSUS_HONOURED', 0),
       ('LAST_CENSUS_ACKNOWLEDGED', 0),
       ('LAST_CENSUS_PROTECTOR', 0),
       ('LAST_CENSUS_DONOR', 0),
       ('LAST_CENSUS_SCOUT', 0);