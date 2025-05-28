CREATE TABLE scout_record
(
    id           INT AUTO_INCREMENT NOT NULL,
    record_type  VARCHAR(255)       NULL,
    start_date   date               NULL,
    end_date     date               NULL,
    observations TEXT               NULL,
    scout_id     INT                NOT NULL,
    CONSTRAINT PK_SCOUT_RECORD PRIMARY KEY (id),
    CONSTRAINT FK_SCOUT_RECORD_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);

CREATE TABLE scout_record_files
(
    scout_record_id INT NOT NULL,
    files_id        INT NOT NULL,
    CONSTRAINT UC_SCOUT_RECORD_FILES_FILES UNIQUE (files_id),
    CONSTRAINT FK_SCOUT_RECORD_FILE_ON_SCOUT_FILE FOREIGN KEY (files_id) REFERENCES scout_file (id),
    CONSTRAINT FK_SCOUT_RECORD_FILE_ON_SCOUT_RECORD FOREIGN KEY (scout_record_id) REFERENCES scout_record (id)
);