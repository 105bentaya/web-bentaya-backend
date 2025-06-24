CREATE TABLE product_image
(
    id        INT AUTO_INCREMENT NOT NULL,
    uuid      VARCHAR(255)       NOT NULL,
    name      VARCHAR(255)       NOT NULL,
    mime_type VARCHAR(255)       NOT NULL,
    CONSTRAINT PK_PRODUCT_IMAGE PRIMARY KEY (id)
);

CREATE TABLE product
(
    id          INT AUTO_INCREMENT NOT NULL,
    name        VARCHAR(255)       NOT NULL,
    image_id    INT                NOT NULL,
    description VARCHAR(1023)      NOT NULL,
    price       INT                NOT NULL,
    CONSTRAINT PK_PRODUCT PRIMARY KEY (id),
    CONSTRAINT FK_IMAGE_ID_ON_PRODUCT_PRODUCT_IMAGE FOREIGN KEY (image_id) REFERENCES product_image (id)
);

CREATE TABLE product_size
(
    id         INT AUTO_INCREMENT NOT NULL,
    product_id INT                NOT NULL,
    size       VARCHAR(255)       NULL,
    stock      INT                NOT NULL,
    CONSTRAINT PK_PRODUCT_SIZE PRIMARY KEY (id),
    CONSTRAINT FK_PRODUCT_SIZE_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE TABLE cart_product
(
    user_id         INT NOT NULL,
    product_size_id INT NOT NULL,
    count           INT NOT NULL,
    CONSTRAINT PK_CART_PRODUCT PRIMARY KEY (user_id, product_size_id),
    CONSTRAINT FK_CART_PRODUCT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT FK_CART_PRODUCT_ON_PRODUCT_SIZE FOREIGN KEY (product_size_id) REFERENCES product_size (id)
);

CREATE TABLE shop_purchase
(
    id           INT AUTO_INCREMENT NOT NULL,
    name         VARCHAR(255)       NULL,
    surname      VARCHAR(255)       NULL,
    email        VARCHAR(255)       NULL,
    phone        VARCHAR(255)       NULL,
    observations TEXT               NULL,
    user_id      INT                NOT NULL,
    CONSTRAINT PK_SHOP_PURCHASE PRIMARY KEY (id),
    CONSTRAINT FK_SHOP_PURCHASE_ON_USER FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE shop_purchase_payment
(
    payment_id       INT NOT NULL,
    shop_purchase_id INT NOT NULL,
    CONSTRAINT PK_SHOP_PURCHASE_PAYMENT PRIMARY KEY (shop_purchase_id, payment_id),
    CONSTRAINT FK_SHOP_PURCHASE_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payment (id),
    CONSTRAINT FK_SHOP_PURCHASE_ON_SHOP_PURCHASE FOREIGN KEY (shop_purchase_id) REFERENCES shop_purchase (id),
    CONSTRAINT UC_SHOP_PURCHASE_PAYMENT_PAYMENT UNIQUE (payment_id),
    CONSTRAINT UC_SHOP_PURCHASE_PAYMENT_SHOP_PURCHASE UNIQUE (shop_purchase_id)
);

CREATE TABLE bought_product
(
    id               INT AUTO_INCREMENT NOT NULL,
    product_name     VARCHAR(255)       NOT NULL,
    size_name        VARCHAR(255)       NOT NULL,
    count            INT                NOT NULL,
    price            INT                NOT NULL,
    shop_purchase_id INT                NOT NULL,
    CONSTRAINT PK_BOUGHT_PRODUCT PRIMARY KEY (id),
    CONSTRAINT FK_BOUGHT_PRODUCT_ON_SHOP_PURCHASE FOREIGN KEY (shop_purchase_id) REFERENCES shop_purchase (id)
);