INSERT INTO service_packages (sort_order, name, default_cost)
VALUES (1, 'Traditional One', 0),
       (2, 'Traditional Two', 0),
       (3, 'Cremation One', 0),
       (4, 'Cremation Two', 0),
       (5, 'Cremation Three', 0),
       (6, 'Cremation Four', 0),
       (7, 'Cremation Five', 0);

INSERT INTO services (sort_order, name, default_cost, included_in_package)
VALUES (1, 'Basic Services of Funeral Director & Staff', 0, FALSE),
       (2, 'Embalming', 0, TRUE),
       (3, 'Other Preparation of Body', 0, FALSE),
       (4, 'Use of Facilities & Staff for Visitation', 0, TRUE),
       (5, 'Use of Facilities & Staff for Funeral Service', 0, TRUE),
       (6, 'Use of Facilities & Staff for Memorial Service', 0, FALSE),
       (7, 'Use of Equipment & Staff for Graveside Service', 0, TRUE),
       (8, 'Funeral Coach', 0, FALSE),
       (9, 'Pallbearer Car', 0, TRUE),
       (10, 'Service Car', 0, FALSE),
       (11, 'Transfer of Remains to Funeral Home', 0, FALSE),
       (12, 'Other', 0, FALSE),
       (13, 'Other', 0, TRUE);

INSERT INTO merchandise (sort_order, name, default_cost, requires_description, sales_taxable, pricing_mode)
VALUES (1, 'Casket or (alternative container)', NULL, TRUE, TRUE, 'flat'),
       (2, 'Cremation Urn', NULL, TRUE, TRUE, 'flat'),
       (3, 'Outer Burial Container', NULL, TRUE, FALSE, 'flat'),
       (4, 'Service Accessory Package', 0, FALSE, FALSE, 'flat'),
       (5, 'Register Book', 0, TRUE, FALSE, 'flat'),
       (6, 'Thank You Cards', 0, FALSE, FALSE, 'flat'),
       (7, 'Memorial Folders', 0, TRUE, FALSE, 'flat'),
       (8, 'Memorial Video', 15.00, FALSE, FALSE, 'per_unit'),
       (9, 'Jewelry', NULL, TRUE, TRUE, 'flat'),
       (10, 'Supervision of Burial', 0, FALSE, FALSE, 'flat'),
       (11, 'Temporary Grave Marker', 0, FALSE, FALSE, 'flat'),
       (12, 'Other Merchandise', NULL, TRUE, TRUE, 'flat'),
       (13, 'Other Merchandise', NULL, TRUE, TRUE, 'flat');

INSERT INTO special_charges (sort_order, name, default_cost, requires_description)
VALUES (1, 'Grave Service Setup/Delivery', NULL, TRUE),
       (2, 'Direct Cremation', 0, FALSE),
       (3, 'Mileage', NULL, TRUE),
       (4, 'Forward of remains to (funeral home)', 0, FALSE),
       (5, 'Receiving of remains from (funeral home)', 0, FALSE),
       (6, 'Vault Company Weekend/Holiday Charge', 0, FALSE),
       (7, 'Immediate Burial', 0, FALSE),
       (8, 'Other', NULL, TRUE);

INSERT INTO cash_advances (sort_order, name)
VALUES (1, 'Grave Opening'),
       (2, 'Weekend/Holiday Charge'),
       (3, 'Newspaper Notices'),
       (4, 'Newspaper Notices'),
       (5, 'Newspaper Notices'),
       (6, 'Newspaper Notices'),
       (7, 'Radio Notices'),
       (8, 'Honorarium Minister'),
       (9, 'Honorarium Minister'),
       (10, 'Honorarium Organist'),
       (11, 'Honorarium Singer'),
       (12, 'Honorarium Singer'),
       (13, 'Honorarium Singer'),
       (14, 'Honorarium Hairdresser'),
       (15, 'Certified Death Certificate'),
       (16, 'Out of Town Mortuary Charges'),
       (17, 'Date for Cemetery Marker'),
       (18, 'Flowers'),
       (19, 'Other'),
       (20, 'Other');
