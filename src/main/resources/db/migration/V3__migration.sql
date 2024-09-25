create table payment (
    id int primary key auto_increment,
    name varchar(255) not null,
    dni varchar(255),
    status int not null,
    payment_type varchar(255) not null,
    modification_date datetime
);

insert into role(id, name) values (5,'ROLE_TRANSACTION');
