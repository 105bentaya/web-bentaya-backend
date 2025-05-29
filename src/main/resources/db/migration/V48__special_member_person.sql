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

ALTER TABLE special_member
    DROP FOREIGN KEY FK_SPECIAL_MEMBER_ON_ID_DOCUMENT,
    DROP COLUMN company_name,
    DROP COLUMN email,
    DROP COLUMN id_document_id,
    DROP COLUMN name,
    DROP COLUMN phone,
    DROP COLUMN surname,
    DROP COLUMN type,
    ADD person_id INT NULL,
    ADD CONSTRAINT FK_SPECIAL_MEMBER_ON_PERSON FOREIGN KEY (person_id) REFERENCES special_member_person (id),
    ADD CONSTRAINT UC_SPECIAL_MEMBER_ROLE_AND_ROLE_CENSUS UNIQUE (role, role_census),
    ADD CONSTRAINT UC_SPECIAL_MEMBER_ROLE_AND_SCOUT_ID UNIQUE (role, scout_id),
    ADD CONSTRAINT UC_SPECIAL_MEMBER_ROLE_AND_PERSON_ID UNIQUE (role, person_id);

INSERT INTO setting(name, value)
VALUES ('LAST_CENSUS_FOUNDER', 0),
       ('LAST_CENSUS_HONOURED', 0),
       ('LAST_CENSUS_ACKNOWLEDGED', 0),
       ('LAST_CENSUS_PROTECTOR', 0),
       ('LAST_CENSUS_DONOR', 0),
       ('LAST_CENSUS_SCOUT', 0)

