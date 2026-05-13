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

CREATE TABLE package_services
(
    package_id INT NOT NULL REFERENCES service_packages (id),
    service_id INT NOT NULL REFERENCES services (id),
    PRIMARY KEY (package_id, service_id)
);
