

CREATE TABLE IF NOT EXISTS storage
(
    stor_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS item
(
    item_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    ean                 varchar(13),
    general_name        varchar(2048),
    product_name        varchar(2048) NOT NULL,
    brand               varchar(2028),
    quantity_current    INT NOT NULL,
    qunatity_total      INT NOT NULL,
    unit                varchar NOT NULL,
    expire_date         DATE NOT NULL,
    description         varchar(10000),
    price_in_cent       INT,
    stor_id             BIGINT,
    FOREIGN KEY (stor_id) REFERENCES storage(stor_id)
);

CREATE TABLE IF NOT EXISTS alwaysInStockItem
(
    item_id             BIGINT PRIMARY KEY,
    minimum_quantity    INT NOT NULL,
    FOREIGN KEY (item_id) REFERENCES item(item_id)
);

CREATE TABLE IF NOT EXISTS ingredient
(
    ingr_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       varchar(1048) NOT NULL
);

CREATE TABLE IF NOT EXISTS consists_of
(
    item_id     BIGINT PRIMARY KEY,
    ingr_id     BIGINT PRIMARY KEY,
    FOREIGN KEY (item_id) REFERENCES item(item_id),
    FOREIGN KEY (ingr_id) REFERENCES ingredient(ingr_id),
);

