ALTER TABLE setting
    DROP COLUMN can_be_null;
ALTER TABLE setting
    DROP COLUMN type;

INSERT INTO setting(name, value)
VALUES ('BOOKING_DATE', '2025-09-30T00:00:00.000Z'),
       ('BOOKING_MIN_DAY_NUMBER', '9'),
       ('BOOKING_MAX_DAY_NUMBER', '21');
