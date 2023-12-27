insert into ROLE_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('ADMIN','Administrator', 1);
insert into ROLE_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('MERCHANT', 'Merchant', 1);

insert into MERCHANT_STATUS_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('ACTIVE', 'Active', 1);
insert into MERCHANT_STATUS_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('INACTIVE', 'Inactive', 1);


insert into TRANSACTION_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('AUTHORIZE', 'Authorize', 1);
insert into TRANSACTION_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('CHARGE', 'Charge', 1);
insert into TRANSACTION_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('REFUND', 'Refund', 1);
insert into TRANSACTION_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('REVERSAL', 'Reversal', 1);

insert into TRANSACTION_STATUS_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('APPROVED', 'Approved', 1);
insert into TRANSACTION_STATUS_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('REVERSED', 'Reversed', 1);
insert into TRANSACTION_STATUS_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('REFUNDED', 'Refunded', 1);
insert into TRANSACTION_STATUS_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('ERROR', 'Error', 1);

insert into MERCHANT_IDENTIFIER_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('EIK_BG', 'EIK (BG)', 1);
insert into MERCHANT_IDENTIFIER_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('BTBSNUM_DE', 'Betriebsnummer (DE)', 1);
insert into MERCHANT_IDENTIFIER_TYPE (`ID`, `NAME`, `VERSION`) VALUES ('CORPNUM_US', 'Corporate Number (USA)', 1);

insert into USER_LOGIN (`ID`, `USERNAME`, `CURRENT_PASSWORD`, `ROLE_ID`, `ENABLED`, `EXPIRED`, `LOCKED`, `HAS_LOGGED_OUT`, `REQUIRE_PASSWORD_CHANGE`, `VERSION`) VALUES (1, 'admin', '{bcrypt}$2a$10$tIB8U.qNvLgtiRByQWM/m.m3kqu0acLGSp4r/vhjZY9B2G.orPSri', 'ADMIN', 'TRUE', 'FALSE', 'FALSE', 'FALSE', 'FALSE', 1);