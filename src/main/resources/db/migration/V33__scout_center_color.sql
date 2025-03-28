ALTER TABLE scout_center
    ADD COLUMN color VARCHAR(7) DEFAULT '#ff00ff' NOT NULL;

ALTER TABLE scout_center
    ALTER color DROP DEFAULT;
