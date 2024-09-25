create table booking
(
    id                       int primary key auto_increment,
    user_id                  int references user (id),
    status                   varchar(255),
    scout_center             varchar(255),
    organization_name        varchar(255),
    cif                      varchar(255),
    packs                    int,
    facility_use             varchar(511),
    contact_name             varchar(255),
    contact_relationship     varchar(255),
    contact_mail             varchar(255),
    contact_phone            varchar(255),
    start_date               datetime,
    end_date                 datetime,
    observations             varchar(1023),
    exclusive_reservation    boolean,
    creation_date            datetime,
    status_observations      varchar(2047),
    user_confirmed_documents boolean not null,
    price                    float
);

create table booking_document
(
    id         int primary key auto_increment,
    booking_id int references booking (id),
    status     varchar(15)  not null,
    file_name  varchar(511) not null,
    file_data  mediumblob
);

insert into role(id, name)
values (8, 'ROLE_SCOUT_CENTER_REQUESTER');
insert into role(id, name)
values (9, 'ROLE_SCOUT_CENTER_MANAGER');