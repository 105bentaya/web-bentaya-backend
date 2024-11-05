CREATE TABLE invoice_expense_type
(
    id           int primary key auto_increment,
    expense_type varchar(255) not null
);

CREATE TABLE invoice_payer
(
    id       int primary key auto_increment,
    payer    varchar(255) not null,
    group_id tinyint
);

CREATE TABLE invoice_grant
(
    id         int primary key auto_increment,
    grant_name varchar(255) not null
);

CREATE TABLE invoice
(
    id              int primary key auto_increment,
    invoice_date    date         not null,
    issuer          varchar(255) not null,
    invoice_number  varchar(255) not null,
    nif             varchar(255) not null,
    amount          int          not null,
    receipt         boolean default false,
    complies        boolean default false,
    payment_date    date         not null,
    method          varchar(255) not null,
    liquidated      boolean default false,
    observations    varchar(4095),
    expense_type_id int          not null references invoice_expense_type (id),
    grant_id        int references invoice_grant (id),
    payer_id        int          not null references invoice_payer (id)
);

INSERT INTO invoice_expense_type(id, expense_type)
VALUES (1, 'Mantenimiento y Reparaciones'),
       (2, 'Servicios profesionales'),
       (3, 'Transportes'),
       (4, 'Seguros'),
       (5, 'Servicios bancarios (- o +)'),
       (6, 'Publicidad y relaciones públicas'),
       (7, 'Agua'),
       (8, 'Energía'),
       (9, 'Telecomunicaciones'),
       (10, 'Salud'),
       (11, 'Alimentación'),
       (12, 'Alojamiento'),
       (13, 'Entradas y accesos'),
       (14, 'Combustibles'),
       (15, 'Material no inventariable y Utillaje'),
       (16, 'Secretaria y Gestión'),
       (17, 'Campañas Financieras'),
       (18, 'Censos y cuotas asociativas'),
       (19, 'Participación Actividades'),
       (20, 'Formación'),
       (21, 'Tasas, Impuestos y Similares'),
       (22, 'Pérdidas de gestión'),
       (23, 'Representación'),
       (24, 'Pérdidas cuotas incobrables'),
       (25, 'Préstamos interseses (- o +)'),
       (26, 'Financieros (- o +)'),
       (27, 'Otros (gastos excepcionales)'),
       (28, 'Material inventariable y Equipos'),
       (29, 'Reducción Asoc Deudores'),
       (30, 'Préstamos capitales banca- amort'),
       (31, 'Préstamos capitales otros - amort'),
       (32, 'Liquidaciones y devoluciones'),
       (33, 'Traspasos Cuentas -');

INSERT INTO invoice_grant(id, grant_name)
VALUES (1, 'DGJ'),
       (2, 'CGC Juventud'),
       (3, 'CGC Participación'),
       (4, 'CGC Convivencia'),
       (5, 'CGC Igualdad'),
       (6, 'CGC Transporte'),
       (7, 'CGC Otras'),
       (8, 'IDEO'),
       (9, 'AYTO. LPGC'),
       (10, 'SAGULPA'),
       (11, 'ASDE 0,7'),
       (12, 'ASDE Otras'),
       (13, 'OTRAS');

INSERT INTO invoice_payer(id, payer, group_id)
VALUES (1, 'GARAJONAY', 1),
       (2, 'WAIGUNGA', 2),
       (3, 'BAOBAB', 3),
       (4, 'AUTINDANA', 4),
       (5, 'ARTETEIFAC', 5),
       (6, 'ARIDANE', 6),
       (7, 'IDAFE', 7),
       (8, 'CV', null),
       (9, 'ACTIVIDAD DE GRUPO', null),
       (10, 'GRUPO GENERAL', null);


