ALTER TABLE event
    ADD meeting_location VARCHAR(255),
    ADD pickup_location  VARCHAR(255),
    DROP latitude,
    DROP longitude
