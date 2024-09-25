create table senior_form (
    id int primary key auto_increment,
    name varchar(255) not null,
    surname varchar(255) not null,
    email varchar(511) not null,
    phone varchar(63) not null,
    accept_message_group boolean not null,
    accept_newsletter boolean not null,
    observations varchar(511)
);