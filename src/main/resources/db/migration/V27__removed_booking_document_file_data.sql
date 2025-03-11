ALTER TABLE booking_document
    MODIFY file_uuid VARCHAR(255) NOT NULL;

ALTER TABLE booking_document
    DROP file_data