alter table event
    modify column title varchar(128) character set utf8mb4 collate utf8mb4_unicode_ci;
alter table event
    modify column description varchar(4095) character set utf8mb4 collate utf8mb4_unicode_ci;
alter table confirmation
    modify column text varchar(1024) character set utf8mb4 collate utf8mb4_unicode_ci;