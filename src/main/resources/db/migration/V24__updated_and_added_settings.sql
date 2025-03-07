ALTER TABLE setting
    ADD type        VARCHAR(255),
    ADD can_be_null BIT DEFAULT 0;

UPDATE setting
SET type = 'BOOLEAN'
WHERE id = 2;

UPDATE setting
SET type        = 'DATE',
    value       = '',
    can_be_null = true
WHERE id = 4;

UPDATE setting
SET type = 'NUMBER'
WHERE id = 1
   OR id = 3;

INSERT INTO setting(id, name, value, type)
VALUES (5, 'BOOKING_MAIL', 'fake_mail@fakebentaya.org', 'STRING'),
       (6, 'CONTACT_MAIL', 'fake_mail@fakebentaya.org', 'STRING'),
       (7, 'COMPLAINT_MAIL', 'fake_mail@fakebentaya.org,fake_mail2@fakebentaya.org', 'STRING'),
       (8, 'FORM_MAIL', 'fake_mail@fakebentaya.org', 'STRING'),
       (9, 'TREASURY_MAIL', 'fake_mail@fakebentaya.org', 'STRING'),
       (10, 'ADMINISTRATION_MAIL', 'fake_mail@fakebentaya.org', 'STRING');

ALTER TABLE setting
    MODIFY type VARCHAR(255) NOT NULL;
ALTER TABLE setting
    MODIFY value VARCHAR(4095) NOT NULL;