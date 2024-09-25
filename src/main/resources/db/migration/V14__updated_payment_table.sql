drop table payment;

create table payment
(
    id                int primary key auto_increment,
    order_number      varchar(15),
    status            int          not null,
    amount            int          not null,
    payment_type      varchar(255) not null,
    modification_date datetime
);
