alter table scout add image_authorization boolean not null default true;
alter table scout alter image_authorization drop default;
alter table scout add shirt_size varchar(64);
alter table scout add municipality varchar(256);
alter table scout add progressions text;
alter table scout add observations text;
