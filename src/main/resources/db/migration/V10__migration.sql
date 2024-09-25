alter table pre_scout add parents_surname varchar(255) not null;
alter table pre_scout add priority_info varchar(255);
alter table pre_scout add size varchar(31) not null;
alter table pre_scout add inscription_year varchar(15) not null default '2024';
alter table pre_scout alter inscription_year drop default;

update pre_scout SET  priority = 1 WHERE priority LIKE '1%';
update pre_scout SET  priority = 1 WHERE priority LIKE '2%';
update pre_scout SET  priority = 2 WHERE priority LIKE '3%';
update pre_scout SET  priority = 3 WHERE priority LIKE '4%';
update pre_scout SET  priority = 4 WHERE priority LIKE 'N%';
update pre_scout SET  priority = 4 WHERE priority LIKE 'n%';

alter table pre_scout modify priority int;
