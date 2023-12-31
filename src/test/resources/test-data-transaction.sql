insert into MERCHANT (ID, NAME, EMAIL, STATUS_ID, IDENTIFIER_TYPE_ID, IDENTIFIER_VALUE, TOTAL_TRANSACTION_SUM, VERSION)  VALUES (1, 'Demo Merchant 1', 'merchant1@test.com', 'ACTIVE', 'EIK_BG', '1234567890', 0, 1);


insert into MERCHANT (ID, NAME, EMAIL, STATUS_ID, IDENTIFIER_TYPE_ID, IDENTIFIER_VALUE, TOTAL_TRANSACTION_SUM, VERSION)  VALUES (2, 'Demo Merchant 2', 'merchant2@test.com', 'ACTIVE', 'EIK_BG', '1111111111', 100.23, 1);
-- UUID: 6f683d71-dbcc-41ed-b552-51130c00852c
insert into TRANSACTION (ID, TYPE_ID, AMOUNT, STATUS_ID, MERCHANT_ID, VERSION) VALUES ('6F683D71DBCC41EDB55251130C00852C', 'CHARGE', '100.23', 'APPROVED', 2, 1);

insert into MERCHANT (ID, NAME, EMAIL, STATUS_ID, IDENTIFIER_TYPE_ID, IDENTIFIER_VALUE, TOTAL_TRANSACTION_SUM, VERSION)  VALUES (3, 'Inactive merchant', 'merchant3@test.com', 'INACTIVE', 'EIK_BG', '2222222', 0, 1);

insert into MERCHANT (ID, NAME, EMAIL, STATUS_ID, IDENTIFIER_TYPE_ID, IDENTIFIER_VALUE, TOTAL_TRANSACTION_SUM, VERSION)  VALUES (4, 'Demo Merchant 4', 'merchant4@test.com', 'ACTIVE', 'EIK_BG', '4444444', 0, 1);

insert into MERCHANT (ID, NAME, EMAIL, STATUS_ID, IDENTIFIER_TYPE_ID, IDENTIFIER_VALUE, TOTAL_TRANSACTION_SUM, VERSION)  VALUES (5, 'Demo Merchant 5', 'merchant5@test.com', 'ACTIVE', 'EIK_BG', '555555', 0, 1);
-- UUID: 3d7ae6ed-c794-47d4-ad11-7b0f53f09d6b
insert into TRANSACTION (ID, TYPE_ID, AMOUNT, STATUS_ID, MERCHANT_ID, VERSION) VALUES ('3D7AE6EDC79447D4AD117B0F53F09D6B', 'AUTHORIZE', '22.31', 'APPROVED', 5, 1);

-- UID: 0f45e032-a74f-434f-b00e-e392ab340ab9	
insert into TRANSACTION (ID, TYPE_ID, AMOUNT, STATUS_ID, MERCHANT_ID, VERSION) VALUES ('0F45E032A74F434FB00EE392AB340AB9', 'CHARGE', '22.31', 'ERROR', 5, 1);

insert into USER_LOGIN (ID, USERNAME, CURRENT_PASSWORD, MERCHANT_ID, ROLE_ID, ENABLED, EXPIRED, LOCKED, HAS_LOGGED_OUT, REQUIRE_PASSWORD_CHANGE, VERSION) VALUES (2, 'merchant2', '{bcrypt}$2a$10$tIB8U.qNvLgtiRByQWM/m.m3kqu0acLGSp4r/vhjZY9B2G.orPSri', 2, 'MERCHANT', 'TRUE', 'FALSE', 'FALSE', 'FALSE', 'FALSE', 1);
insert into USER_LOGIN (ID, USERNAME, CURRENT_PASSWORD, MERCHANT_ID, ROLE_ID, ENABLED, EXPIRED, LOCKED, HAS_LOGGED_OUT, REQUIRE_PASSWORD_CHANGE, VERSION) VALUES (3, 'merchant3', '{bcrypt}$2a$10$tIB8U.qNvLgtiRByQWM/m.m3kqu0acLGSp4r/vhjZY9B2G.orPSri', 3, 'MERCHANT', 'TRUE', 'FALSE', 'FALSE', 'FALSE', 'FALSE', 1);
insert into USER_LOGIN (ID, USERNAME, CURRENT_PASSWORD, MERCHANT_ID, ROLE_ID, ENABLED, EXPIRED, LOCKED, HAS_LOGGED_OUT, REQUIRE_PASSWORD_CHANGE, VERSION) VALUES (4, 'merchant4', '{bcrypt}$2a$10$tIB8U.qNvLgtiRByQWM/m.m3kqu0acLGSp4r/vhjZY9B2G.orPSri', 4, 'MERCHANT', 'TRUE', 'FALSE', 'FALSE', 'FALSE', 'FALSE', 1);
insert into USER_LOGIN (ID, USERNAME, CURRENT_PASSWORD, MERCHANT_ID, ROLE_ID, ENABLED, EXPIRED, LOCKED, HAS_LOGGED_OUT, REQUIRE_PASSWORD_CHANGE, VERSION) VALUES (5, 'merchant5', '{bcrypt}$2a$10$tIB8U.qNvLgtiRByQWM/m.m3kqu0acLGSp4r/vhjZY9B2G.orPSri', 5, 'MERCHANT', 'TRUE', 'FALSE', 'FALSE', 'FALSE', 'FALSE', 1);
