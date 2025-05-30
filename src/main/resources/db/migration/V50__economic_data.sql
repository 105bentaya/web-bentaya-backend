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

INSERT INTO economic_data(scout_id)
SELECT id
FROM scout;


