DROP TABLE donor_donation;
DROP TABLE donor;

CREATE TABLE economic_data
(
    scout_id INT          NOT NULL,
    iban     VARCHAR(255) NULL,
    bank     VARCHAR(255) NULL,
    CONSTRAINT PK_ECONOMIC_DATA PRIMARY KEY (scout_id),
    CONSTRAINT FK_ECONOMIC_DATA_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);

CREATE TABLE economic_data_documents
(
    economic_data_scout_id INT NOT NULL,
    documents_id           INT NOT NULL,
    CONSTRAINT UC_ECONOMIC_DATA_DOCUMENTS_DOCUMENTS UNIQUE (documents_id),
    CONSTRAINT FK_ECONOMIC_DATA_ON_ECONOMIC_DATA FOREIGN KEY (economic_data_scout_id) REFERENCES economic_data (scout_id),
    CONSTRAINT FK_ECONOMIC_DATA_ON_SCOUT_FILE FOREIGN KEY (documents_id) REFERENCES scout_file (id)
);

CREATE TABLE economic_entry
(
    id                     INT AUTO_INCREMENT NOT NULL,
    date                   date               NOT NULL,
    description            VARCHAR(255)       NOT NULL,
    amount                 INT                NOT NULL,
    income                 VARCHAR(255)       NULL,
    spending               VARCHAR(255)       NULL,
    account                VARCHAR(255)       NULL,
    type                   VARCHAR(255)       NOT NULL,
    observations           VARCHAR(511)       NULL,
    economic_data_scout_id INT                NOT NULL,
    CONSTRAINT FK_ECONOMIC_ENTRY_ON_ECONOMIC_DATA_SCOUT FOREIGN KEY (economic_data_scout_id) REFERENCES economic_data (scout_id),
    CONSTRAINT PK_ECONOMIC_ENTRY PRIMARY KEY (id)
);

INSERT INTO economic_data(scout_id)
SELECT id
FROM scout;


