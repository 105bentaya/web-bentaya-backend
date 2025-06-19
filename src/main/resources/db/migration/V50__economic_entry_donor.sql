CREATE TABLE economic_entry_donor
(
    economic_entry_id INT          NOT NULL,
    name              VARCHAR(255) NOT NULL,
    surname           VARCHAR(255) NULL,
    id_document_id    INT          NOT NULL,
    person_type       VARCHAR(255) NOT NULL,
    CONSTRAINT PK_ECONOMIC_ENTRY_DONOR PRIMARY KEY (economic_entry_id),
    CONSTRAINT FK_ECONOMIC_ENTRY_DONOR_ON_ECONOMIC_ENTRY FOREIGN KEY (economic_entry_id) REFERENCES economic_entry (id),
    CONSTRAINT FK_ECONOMIC_ENTRY_DONOR_ON_ID_DOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id)
);


UPDATE economic_entry
SET type = 'DONATION'
WHERE type = 'Donación';

UPDATE economic_entry
SET type = 'PAYMENT'
WHERE type = 'Pago';

UPDATE economic_entry
SET type = 'CONTRIBUTION'
WHERE type = 'Aportación';

UPDATE economic_entry
SET type = 'CHARGE'
WHERE type NOT IN ('DONATION', 'PAYMENT', 'CHARGE');