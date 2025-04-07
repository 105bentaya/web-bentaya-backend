ALTER TABLE booking_document
    DROP FOREIGN KEY FK_BOOKINGDOCUMENT_ON_BOOKING;

ALTER TABLE booking
    DROP FOREIGN KEY FK_BOOKING_ON_INCIDENCES_FILE;

ALTER TABLE booking
    DROP FOREIGN KEY FK_BOOKING_ON_USER;

CREATE TABLE general_booking
(
    id                       INT           NOT NULL,
    user_id                  INT           NOT NULL,
    organization_name        VARCHAR(255)  NULL,
    cif                      VARCHAR(255)  NOT NULL,
    facility_use             VARCHAR(2000) NULL,
    group_description        VARCHAR(511)  NULL,
    contact_name             VARCHAR(255)  NULL,
    contact_relationship     VARCHAR(255)  NULL,
    contact_mail             VARCHAR(255)  NULL,
    contact_phone            VARCHAR(255)  NULL,
    price                    FLOAT         NULL,
    user_confirmed_documents BIT(1)        NOT NULL,
    finished                 BIT(1)        NOT NULL,
    incidences_file_id       INT           NULL,
    CONSTRAINT pk_generalbooking PRIMARY KEY (id)
);

INSERT INTO general_booking(id, user_id, organization_name, cif, facility_use, group_description, contact_name,
                            contact_relationship, contact_mail, contact_phone, price, user_confirmed_documents,
                            finished, incidences_file_id)
SELECT id,
       user_id,
       organization_name,
       cif,
       facility_use,
       group_description,
       contact_name,
       contact_relationship,
       contact_mail,
       contact_phone,
       price,
       user_confirmed_documents,
       finished,
       incidences_file_id
FROM booking
WHERE booking.own_booking = false;

ALTER TABLE booking_document
    ADD CONSTRAINT FK_BOOKINGDOCUMENT_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES general_booking (id);

ALTER TABLE general_booking
    ADD CONSTRAINT FK_GENERALBOOKING_ON_ID FOREIGN KEY (id) REFERENCES booking (id);

ALTER TABLE general_booking
    ADD CONSTRAINT FK_GENERALBOOKING_ON_INCIDENCESFILE FOREIGN KEY (incidences_file_id) REFERENCES booking_document_file (id);

ALTER TABLE general_booking
    ADD CONSTRAINT FK_GENERALBOOKING_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);


CREATE TABLE own_booking
(
    id       INT NOT NULL,
    group_id INT NULL,
    CONSTRAINT pk_ownbooking PRIMARY KEY (id)
);

INSERT INTO own_booking(id)
SELECT id
FROM booking
WHERE own_booking = true;

ALTER TABLE own_booking
    ADD CONSTRAINT FK_OWNBOOKING_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id);

ALTER TABLE own_booking
    ADD CONSTRAINT FK_OWNBOOKING_ON_ID FOREIGN KEY (id) REFERENCES booking (id);

ALTER TABLE booking
    DROP COLUMN cif,
    DROP COLUMN contact_mail,
    DROP COLUMN contact_name,
    DROP COLUMN contact_phone,
    DROP COLUMN contact_relationship,
    DROP COLUMN facility_use,
    DROP COLUMN finished,
    DROP COLUMN incidences_file_id,
    DROP COLUMN organization_name,
    DROP COLUMN own_booking,
    DROP COLUMN price,
    DROP COLUMN user_confirmed_documents,
    DROP COLUMN user_id,
    DROP COLUMN group_description;

ALTER TABLE booking_document
    DROP COLUMN observations;
