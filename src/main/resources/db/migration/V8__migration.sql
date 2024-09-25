alter table event add unknown_time boolean not null default false;
alter table event alter unknown_time drop default;
