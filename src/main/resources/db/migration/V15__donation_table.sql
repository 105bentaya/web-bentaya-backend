create table donation
(
    id                           int primary key auto_increment,
    name                         varchar(255) not null,
    first_surname                varchar(255) not null,
    second_surname               varchar(255),
    cif                          varchar(31)  not null,
    phone                        varchar(255) not null,
    email                        varchar(511) not null,
    deduct                       boolean      not null,
    amount                       int          not null,
    frequency                    varchar(63),
    single_donation_payment_type varchar(63),
    iban                         varchar(255),
    creation_date            datetime     not null
);

create table donation_payment
(
    donation_id int references donation (id),
    payment_id  int references payment (id)
);