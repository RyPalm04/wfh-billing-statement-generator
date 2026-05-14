CREATE TABLE service_packages
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    sort_order   INT            NOT NULL,
    name         VARCHAR(80)    NOT NULL,
    default_cost DECIMAL(10, 2) NOT NULL DEFAULT 0
);

CREATE TABLE services
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    sort_order          INT            NOT NULL,
    name                VARCHAR(120)   NOT NULL,
    default_cost        DECIMAL(10, 2) NOT NULL DEFAULT 0,
    included_in_package BOOLEAN        NOT NULL DEFAULT FALSE
);

CREATE TABLE merchandise
(
    id                   INT PRIMARY KEY AUTO_INCREMENT,
    sort_order           INT         NOT NULL,
    name                 VARCHAR(80) NOT NULL,
    default_cost         DECIMAL(10, 2),
    requires_description BOOLEAN     NOT NULL DEFAULT FALSE,
    sales_taxable        BOOLEAN     NOT NULL DEFAULT FALSE,
    pricing_mode         VARCHAR(20) NOT NULL DEFAULT 'flat'
);

CREATE TABLE special_charges
(
    id                   INT PRIMARY KEY AUTO_INCREMENT,
    sort_order           INT         NOT NULL,
    name                 VARCHAR(80) NOT NULL,
    default_cost         DECIMAL(10, 2),
    requires_description BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE cash_advances
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    sort_order INT         NOT NULL,
    name       VARCHAR(80) NOT NULL
);

CREATE TABLE packaged_services
(
    package_id INT NOT NULL REFERENCES service_packages (id),
    service_id INT NOT NULL REFERENCES services (id),
    PRIMARY KEY (package_id, service_id)
);

CREATE TABLE saved_statements
(
    id                   INT PRIMARY KEY AUTO_INCREMENT,
    control_number       INT            NOT NULL UNIQUE,
    services_for_name    VARCHAR(120)   NOT NULL DEFAULT '',
    date_of_death        DATE,
    place_of_death       VARCHAR(120)   NOT NULL DEFAULT '',
    service_date         DATE,
    reason_for_embalming VARCHAR(255)   NOT NULL DEFAULT '',
    package_id           INT REFERENCES service_packages (id),
    sales_tax_rate       DECIMAL(6, 4)  NOT NULL DEFAULT 0,
    payment              DECIMAL(10, 2) NOT NULL DEFAULT 0,
    saved_at             TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE saved_statement_services
(
    statement_id INT     NOT NULL REFERENCES saved_statements (id) ON DELETE CASCADE,
    service_id   INT     NOT NULL REFERENCES services (id),
    in_package   BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (statement_id, service_id)
);

CREATE TABLE saved_statement_merchandise
(
    statement_id   INT          NOT NULL REFERENCES saved_statements (id) ON DELETE CASCADE,
    merchandise_id INT          NOT NULL REFERENCES merchandise (id),
    price          DECIMAL(10, 2),
    description    VARCHAR(255) NOT NULL DEFAULT '',
    PRIMARY KEY (statement_id, merchandise_id)
);

CREATE TABLE saved_statement_special_charges
(
    statement_id      INT          NOT NULL REFERENCES saved_statements (id) ON DELETE CASCADE,
    special_charge_id INT          NOT NULL REFERENCES special_charges (id),
    price             DECIMAL(10, 2),
    description       VARCHAR(255) NOT NULL DEFAULT '',
    PRIMARY KEY (statement_id, special_charge_id)
);

CREATE TABLE saved_statement_cash_advances
(
    statement_id    INT          NOT NULL REFERENCES saved_statements (id) ON DELETE CASCADE,
    cash_advance_id INT          NOT NULL REFERENCES cash_advances (id),
    amount          DECIMAL(10, 2),
    provider        VARCHAR(120) NOT NULL DEFAULT '',
    PRIMARY KEY (statement_id, cash_advance_id)
);
