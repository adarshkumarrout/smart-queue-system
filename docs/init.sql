-- SmartQueue Sample Data
INSERT IGNORE INTO businesses (name, description, owner_email, is_active, created_at)
VALUES ('City Hospital', 'Multi-specialty hospital', 'admin@cityhospital.com', 1, NOW()),
       ('Quick Serve Bank', 'Retail banking services', 'admin@qsbank.com', 1, NOW());

INSERT IGNORE INTO branches (name, address, city, business_id, is_active, created_at)
VALUES ('Main Branch', '123 MG Road', 'Bengaluru', 1, 1, NOW()),
       ('East Branch',  '456 ORR',     'Bengaluru', 1, 1, NOW());
