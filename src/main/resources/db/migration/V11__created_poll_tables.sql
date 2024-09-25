create table poll
(
    id          int primary key auto_increment,
    name        varchar(255)  not null,
    description varchar(4095) not null
);

create table poll_option
(
    id          int primary key auto_increment,
    name        varchar(255)  not null,
    description varchar(4095) not null,
    poll_id     int           not null references poll (id)
);

create table poll_attachment
(
    id             int primary key auto_increment,
    link    varchar(4095) not null,
    poll_option_id int           not null references poll_option (id)
);

create table poll_vote
(
    id             int primary key auto_increment,
    ip             varchar(255) not null,
    poll_option_id int          not null references poll_option (id)
);
