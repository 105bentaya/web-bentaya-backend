CREATE TABLE special_member_donation
(
    id                    INT AUTO_INCREMENT NOT NULL,
    date                  DATE               NOT NULL,
    type                  VARCHAR(255)       NOT NULL,
    in_kind_donation_type VARCHAR(255)       NULL,
    amount                INT                NULL,
    payment_type          VARCHAR(255)       NULL,
    bank_account          VARCHAR(255)       NULL,
    notes                 VARCHAR(511)       NULL,
    special_member_id     INT                NOT NULL,
    CONSTRAINT PK_SPECIAL_MEMBER_DONATION PRIMARY KEY (id),
    CONSTRAINT FK_SPECIAL_MEMBER_DONATION_ON_SPECIAL_MEMBER FOREIGN KEY (special_member_id) REFERENCES special_member (id)
);
