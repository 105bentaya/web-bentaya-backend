CREATE TABLE user_scouts
(
    user_id  INT NOT NULL,
    scout_id INT NOT NULL,
    CONSTRAINT PK_USER_SCOUTS PRIMARY KEY (user_id, scout_id),
    CONSTRAINT FK_USER_SCOUTS_ON_USER FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FK_USER_SCOUTS_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);

INSERT INTO user_scouts (scout_id, user_id)
SELECT scout_id, user_id
FROM scout_user;

DROP TABLE scout_user;

CREATE TABLE user_scouter
(
    user_id  INT NOT NULL,
    scout_id INT NOT NULL,
    CONSTRAINT PK_USER_SCOUTS PRIMARY KEY (user_id, scout_id),
    CONSTRAINT UC_USER UNIQUE (user_id),
    CONSTRAINT UC_SCOUT UNIQUE (scout_id),
    CONSTRAINT FK_USER_SCOUTER_ON_USER FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FK_USER_SCOUTER_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id)
);


CREATE TEMPORARY TABLE scouter_user_migration AS
SELECT DISTINCT u.id AS user_id, u.group_id, u.username, 0 as scout_id
FROM user u
         INNER JOIN user_role ur ON u.id = ur.user_id
WHERE ur.role_id IN (2, 6)
  AND u.enabled;

ALTER TABLE scout
    ADD COLUMN temp_user_id INT NULL;

INSERT INTO scout (scout_type, status, federated, group_id, temp_user_id)
SELECT 'SCOUTER',
       'ACTIVE',
       true,
       group_id,
       user_id
FROM scouter_user_migration;

UPDATE scouter_user_migration sum
    INNER JOIN scout s ON s.temp_user_id = sum.user_id
SET scout_id = s.id;

ALTER TABLE scout
    DROP COLUMN temp_user_id;


INSERT INTO personal_data(image_authorization, birthday, gender, surname, name, scout_id, email)
SELECT true, '19700101', 'Otro', 'PON TU APELLIDO', 'PON TU NOMBRE', scout_id, username
FROM scouter_user_migration;

INSERT INTO medical_data(scout_id, blood_type)
SELECT scout_id, 'NA'
FROM scouter_user_migration;

INSERT INTO economic_data(scout_id)
SELECT scout_id
FROM scouter_user_migration;

INSERT INTO scout_history(scout_id)
SELECT scout_id
FROM scouter_user_migration;

INSERT INTO user_scouter (user_id, scout_id)
SELECT user_id, scout_id
FROM scouter_user_migration;

DROP TABLE scouter_user_migration;

ALTER TABLE user
    DROP CONSTRAINT FK_USER_ON_GROUP,
    DROP COLUMN group_id;

# ALTER ROLES

CREATE TEMPORARY TABLE roles_to_update_temp
SELECT ur.user_id
FROM user_role ur
WHERE ur.role_id = 6
  AND NOT EXISTS (SELECT 1
                  FROM user_role ur2
                  WHERE ur2.user_id = ur.user_id
                    AND ur2.role_id = 2);

UPDATE user_role ur
SET ur.role_id = 2
WHERE ur.role_id = 6
  AND ur.user_id IN (SELECT rtut.user_id FROM roles_to_update_temp rtut);

DROP TABLE roles_to_update_temp;

DELETE
FROM user_role
WHERE role_id = 6;

UPDATE role r
SET r.name = 'ROLE_SECRETARY'
WHERE r.id = 6;

ALTER TABLE user_role
    ADD CONSTRAINT FK_USER_ROLE_USER_ON_USER FOREIGN KEY (user_id) REFERENCES user (id),
    ADD CONSTRAINT FK_USER_ROLE_ROLE_ON_ROLE FOREIGN KEY (role_id) REFERENCES role (id),
    ADD CONSTRAINT PK_USER_ROLE PRIMARY KEY (user_id, role_id);

ALTER TABLE personal_data
    ADD CONSTRAINT UC_PERSONAL_DATA_EMAIL UNIQUE (email);

ALTER TABLE user
    ADD CONSTRAINT UC_USER_USERNAME UNIQUE (username);