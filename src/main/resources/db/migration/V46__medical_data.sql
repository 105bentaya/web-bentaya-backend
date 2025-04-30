CREATE TABLE insurance_holder
(
    id                         INT AUTO_INCREMENT NOT NULL,
    contact_id                 INT                NULL,
    name                       VARCHAR(255)       NULL,
    surname                    VARCHAR(255)       NULL,
    id_document_id INT                NULL,
    phone                      VARCHAR(255)       NULL,
    email                      VARCHAR(255)       NULL,
    CONSTRAINT pk_insuranceholder PRIMARY KEY (id),
    CONSTRAINT FK_INSURANCEHOLDER_ON_CONTACT FOREIGN KEY (contact_id) REFERENCES scout_contact (id),
    CONSTRAINT FK_INSURANCEHOLDER_ON_IDDOCUMENT FOREIGN KEY (id_document_id) REFERENCES identification_document (id)
);



CREATE TABLE medical_data
(
    scout_id                    INT          NOT NULL,
    blood_type                  VARCHAR(255) NOT NULL,
    social_security_number      VARCHAR(255) NULL,
    social_security_holder_id   INT          NULL,
    private_insurance_number    VARCHAR(255) NULL,
    private_insurance_entity    VARCHAR(255) NULL,
    private_insurance_holder_id INT          NULL,
    food_intolerances           TEXT         NULL,
    food_allergies              TEXT         NULL,
    food_problems               TEXT         NULL,
    food_diet                   TEXT         NULL,
    food_medication             TEXT         NULL,
    medical_intolerances        TEXT         NULL,
    medical_allergies           TEXT         NULL,
    medical_diagnoses           TEXT         NULL,
    medical_precautions         TEXT         NULL,
    medical_medications         TEXT         NULL,
    medical_emergencies         TEXT         NULL,
    addictions                  TEXT         NULL,
    tendencies                  TEXT         NULL,
    records                     TEXT         NULL,
    bullying_protocol           TEXT         NULL,
    CONSTRAINT pk_medicaldata PRIMARY KEY (scout_id),
    CONSTRAINT FK_MEDICALDATA_ON_PRIVATEINSURANCEHOLDER FOREIGN KEY (private_insurance_holder_id) REFERENCES insurance_holder (id),
    CONSTRAINT FK_MEDICALDATA_ON_SCOUT FOREIGN KEY (scout_id) REFERENCES scout (id),
    CONSTRAINT FK_MEDICALDATA_ON_SOCIALSECURITYHOLDER FOREIGN KEY (social_security_holder_id) REFERENCES insurance_holder (id)
);


CREATE TABLE medical_data_documents
(
    medical_data_scout_id INT NOT NULL,
    documents_id          INT NOT NULL,
    CONSTRAINT uc_medical_data_food_files_foodfiles UNIQUE (documents_id),
    CONSTRAINT fk_meddatfoofil_on_medical_data FOREIGN KEY (medical_data_scout_id) REFERENCES medical_data (scout_id),
    CONSTRAINT fk_meddatfoofil_on_member_file FOREIGN KEY (documents_id) REFERENCES member_file (id)
);

INSERT INTO medical_data(scout_id, blood_type, medical_diagnoses)
SELECT id, 'NA', medical_data_old
FROM scout;


ALTER TABLE scout
    DROP medical_data_old;