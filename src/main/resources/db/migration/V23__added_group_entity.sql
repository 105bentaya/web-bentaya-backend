CREATE TABLE bentaya_group
(
    id          INT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255)       NULL,
    email       VARCHAR(255)       NULL,
    group_order INT                NOT NULL UNIQUE,
    CONSTRAINT pk_group PRIMARY KEY (id)
);

# GRUPOS

INSERT INTO bentaya_group
VALUES (1, 'Garajonay', 'fake_mail@fake_bentaya.org', 1),
       (2, 'Waigunga', 'fake_mail@fake_bentaya.org', 2),
       (3, 'Baobab', 'fake_mail@fake_bentaya.org', 3),
       (4, 'Autindana', 'fake_mail@fake_bentaya.org', 5),
       (5, 'Arteteifac', 'fake_mail@fake_bentaya.org', 4),
       (6, 'Aridane', 'fake_mail@fake_bentaya.org', 6),
       (7, 'Idafe', 'fake_mail@fake_bentaya.org', 7);

# ACTUALIZACIONES

#---------------EVENTOS---------------#

ALTER TABLE event
    ADD for_scouters BIT(1) DEFAULT 0;

ALTER TABLE event
    ADD for_everyone BIT(1) DEFAULT 0;

# Actualizamos los eventos de grupo a que no tengan 'group'
UPDATE event
SET group_id     = NULL,
    for_everyone = 1
WHERE group_id = 0;

# Actualizamos los eventos de scouters a que no tengan 'group' con 'for_scouter' a true
UPDATE event
SET group_id     = NULL,
    for_everyone = 1,
    for_scouters = 1
WHERE group_id = 8;


ALTER TABLE event
    MODIFY group_id INT NULL;

ALTER TABLE event
    ADD CONSTRAINT FK_EVENT_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id);

#-------------------------------------#

ALTER TABLE invoice_payer
    MODIFY group_id INT NULL;

ALTER TABLE invoice_payer
    ADD CONSTRAINT FK_INVOICEPAYER_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id);

#-------------------------------------#

ALTER TABLE pre_scout_assignation
    MODIFY group_id INT NOT NULL;

ALTER TABLE pre_scout_assignation
    ADD CONSTRAINT FK_PRESCOUTASSIGNATION_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id);

#-------------------------------------#

ALTER TABLE scout
    MODIFY group_id INT NOT NULL;

ALTER TABLE scout
    ADD CONSTRAINT FK_SCOUT_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id);

#-------------------------------------#

ALTER TABLE user
    MODIFY group_id INT NULL;

ALTER TABLE user
    ADD CONSTRAINT FK_USER_ON_GROUP FOREIGN KEY (group_id) REFERENCES bentaya_group (id);


