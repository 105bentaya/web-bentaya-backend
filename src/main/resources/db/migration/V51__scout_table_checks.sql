ALTER TABLE scout
    ADD CONSTRAINT CHK_SCOUT_SCOUT_TYPE_VALID
        CHECK (scout_type IN ('SCOUT', 'SCOUTER', 'COMMITTEE', 'MANAGER', 'INACTIVE'));

ALTER TABLE scout
    ADD CONSTRAINT CHK_SCOUT_STATUS_VALID
        CHECK (status IN ('ACTIVE', 'PENDING_NEW', 'PENDING_EXISTING', 'INACTIVE'));

ALTER TABLE scout
    ADD CONSTRAINT CHK_SCOUT_SCOUT_TYPE_MATCHES_STATUS
        CHECK (
            (scout_type = 'INACTIVE' AND status = 'INACTIVE') OR
            (scout_type <> 'INACTIVE' AND status <> 'INACTIVE')
            );

ALTER TABLE scout
    ADD CONSTRAINT CHK_SCOUT_GROUP_ID_BY_SCOUT_TYPE
        CHECK (
            (scout_type = 'SCOUT' AND group_id IS NOT NULL) OR
            (scout_type IN ('COMMITTEE', 'MANAGER', 'INACTIVE') AND group_id IS NULL) OR
            (scout_type = 'SCOUTER')
            );