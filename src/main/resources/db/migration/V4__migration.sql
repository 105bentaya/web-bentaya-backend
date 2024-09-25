alter table user add column group_id tinyint;

create table scout(
    id int primary key auto_increment,
    group_id tinyint,
    name varchar(128) not null,
    surname varchar(128) not null,
    dni varchar(64),
    birthday timestamp,
    gender varchar(64),
    medical_data varchar(1024)
);

create table contact(
    id int primary key auto_increment,
    name varchar(128) not null,
    relationship varchar(128),
    email varchar(256),
    phone varchar(64),
    scout_id int references scout(id)
);

create table scout_user (
    scout_id int references scout(id),
    user_id int references user(id)
);

create table event(
    id int primary key auto_increment,
    group_id tinyint,
    title varchar(128),
    description varchar(4095) not null,
    start_date datetime,
    end_date datetime,
    latitude varchar(64),
    longitude varchar(64),
    location varchar(256),
    active_attendance_list boolean not null,
    active_attendance_payment boolean not null,
    closed_attendance_list boolean not null
);

create table confirmation(
    scout_id int references scout(id),
    event_id int references event(id),
    attending boolean,
    text varchar(1024),
    payed boolean,
    primary key (scout_id, event_id)
);

create table pre_scout_assignation(
    pre_scout_id int primary key references pre_scout(id),
    status int not null,
    assignation_date datetime not null,
    comment varchar(1024),
    group_id tinyint not null
);

insert into setting(id, name, value) values (3,'currentYear', '2023');
insert into role(id, name) values (6,'ROLE_GROUP_SCOUTER');
insert into role(id, name) values (7,'ROLE_FORM');

