CREATE TABLE invoice_file
(
    id         INT AUTO_INCREMENT NOT NULL,
    uuid       VARCHAR(255)       NOT NULL,
    name       VARCHAR(255)       NOT NULL,
    mime_type  VARCHAR(255)       NOT NULL,
    invoice_id INT                NULL,
    CONSTRAINT pk_invoicefile PRIMARY KEY (id)
);

ALTER TABLE invoice_file
    ADD CONSTRAINT FK_INVOICEFILE_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoice (id);