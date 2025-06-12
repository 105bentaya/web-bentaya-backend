ALTER TABLE invoice_expense_type RENAME COLUMN expense_type TO description;

INSERT INTO invoice_expense_type(id, description)
VALUES (34, 'Personal: salario'),
       (35, 'Personal: seguridad social');


CREATE TABLE invoice_income_type
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE economic_entry
(
    id                     INT AUTO_INCREMENT NOT NULL,
    issue_date             DATE               NOT NULL,
    due_date               DATE               NOT NULL,
    description            VARCHAR(255)       NOT NULL,
    amount                 INT                NOT NULL,
    expense_type_id        INT                NULL,
    income_type_id         INT                NULL,
    account                VARCHAR(255)       NULL,
    type                   VARCHAR(255)       NOT NULL,
    observations           VARCHAR(511)       NULL,
    economic_data_scout_id INT                NOT NULL,
    CONSTRAINT FK_ECONOMIC_ENTRY_ON_ECONOMIC_DATA_SCOUT FOREIGN KEY (economic_data_scout_id) REFERENCES economic_data (scout_id),
    CONSTRAINT FK_ECONOMIC_ENTRY_ON_EXPENSE_TYPE FOREIGN KEY (expense_type_id) REFERENCES invoice_expense_type (id),
    CONSTRAINT FK_ECONOMIC_ENTRY_ON_INCOME_TYPE FOREIGN KEY (income_type_id) REFERENCES invoice_income_type (id),
    CONSTRAINT PK_ECONOMIC_ENTRY PRIMARY KEY (id)
);

INSERT INTO invoice_income_type(id, description)
VALUES (1, 'Cuotas Asoc Inscripciones D (+ o -)'),
       (2, 'Cuotas Asoc Trimestrales D (+ o -)'),
       (3, 'Cuotas Asoc Otras D (+ o -)'),
       (4, 'Ingresos Asoc Actividades (+ o -)'),
       (5, 'Ingresos Asoc Otras y usuarios (+ o -)'),
       (6, 'Campañas Financieras'),
       (7, 'Patrocinios y colaboraciones'),
       (8, 'Donaciones y legados de terceros'),
       (9, 'Subvenciones Públicas'),
       (10, 'Subvenciones Privadas'),
       (11, 'Subvenciones Scouts'),
       (12, 'Otros (ingresos excepcionales)'),
       (13, 'Ingresos Asoc Deudores'),
       (14, 'Préstamos capitales banca +'),
       (15, 'Préstamos capitales otros'),
       (16, 'Liquidaciones y devoluciones'),
       (17, 'Traspasos Cuentas');
