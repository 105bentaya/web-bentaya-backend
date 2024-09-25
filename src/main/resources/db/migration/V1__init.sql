create table pre_scout (
    id int primary key auto_increment,
    age varchar(255),
    birthday varchar(255),
    comment varchar(255),
    creation_date varchar(255),
    dni varchar(255),
    email varchar(255),
    has_been_in_group boolean,
    medical_data varchar(255),
    name varchar(255),
    parents_name varchar(255),
    phone varchar(255),
    priority varchar(255),
    gender varchar(255),
    surname varchar(255),
    year_and_section varchar(255),
    relationship varchar(255),
    section varchar(255)
);

create table pre_scouter (
    id int primary key auto_increment,
    name varchar(255),
    surname varchar(255),
    gender varchar(255),
    email varchar(255),
    phone varchar(255),
    birthday varchar(255),
    comment varchar(255),
    creation_date varchar(255)
);

create table activity_inscription (
    id int primary key auto_increment,
    language varchar(255),
    activity varchar(255),
    name varchar(255),
    gender varchar(255),
    identity_documentation varchar(255),
    contact_name varchar(255),
    phone varchar(255),
    emergency_phone varchar(255),
    email varchar(255),
    emergency_email varchar(255),
    address varchar(255),
    birthday varchar(255),
    scout_source varchar(255),
    medical_data varchar(255),
    nutritional_data varchar(255)
);

create table complaint (
    id int primary key auto_increment,
    category varchar(255),
    type varchar(255),
    name varchar(255),
    phone varchar(255),
    email varchar(255),
    text text
);

create table user (
    id int primary key auto_increment,
    enabled boolean not null,
    username varchar(255) not null,
    password varchar(255) not null
);

create table role (
    id int primary key auto_increment,
    name varchar(255) not null unique
);

create table user_role (
    user_id int references user(id),
    role_id int references role(id)
);

create table contact_message (
    id int primary key auto_increment,
    name varchar(255),
    email varchar(255),
    subject varchar(255) not null,
    message text not null
);

create table blog (
    id int primary key auto_increment,
    title varchar(255) unique,
    description varchar(255),
    image varchar(255),
    data text,
    modification_date datetime,
    end_date datetime,
    event boolean,
    published boolean
);

create table setting (
    id int primary key auto_increment,
    name varchar(255) unique not null,
    value varchar(255) not null
);

insert into user(id, username, password, enabled) values (1,'david.koschel','$2a$12$3ftTSFaH5TswjJRjLxdBvuJCGre5Cc6YK1Iu/b.xn6svze0kFKSRC', true);

insert into role(id, name) values (1,'ROLE_ADMIN');
insert into role(id, name) values (2,'ROLE_SCOUTER');
insert into role(id, name) values (3,'ROLE_USER');
insert into role(id, name) values (4,'ROLE_EDITOR');

insert into setting(id, name, value) values (1,'currentFormYear', '2022');
insert into setting(id, name, value) values (2,'formIsOpen', '0');

insert into user_role(user_id,role_id) values (1,1);
