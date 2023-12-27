package com.example.empay.util;

import com.example.empay.entity.security.RoleType;

import java.time.ZoneId;

/**
 * Constants for this application.
 */
public final class Constants {
    private Constants() {
    }

    /**
     * UTC ZoneId.
     */
    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    /**
     * Database index on table MERCHANT column EMAIL.
     */
    public static final String IDX_MERCHANT_EMAIL = "IDX_MERCHANT_EMAIL";
    /**
     * Database foreign key on table MERCHANT column STATUS_ID.
     */
    public static final String FK_MERCHANT_STATUS_ID = "FK_MERCHANT_STATUS_ID";
    /**
     * Database foreign key on table MERCHANT column IDENTITY_TYPE_ID.
     */
    public static final String FK_MERCHANT_IDENT_TYPE_ID = "FK_MERCHANT_IDENT_TYPE_ID";
    /**
     * Database index on table MERCHANT column IDENTITY_ID.
     */
    public static final String IDX_MERCHANT_IDENT = "IDX_MERCHANT_IDENT";
    /**
     * Database index on table TRANSACTION column STATUS_ID.
     */
    public static final String FK_TRANSACTION_STATUS_ID = "FK_TRANS_STATUS_ID";
    /**
     * Database index on table TRANSACTION column TYPE_ID.
     */
    public static final String FK_TRANSACTION_TYPE_ID = "FK_TRANS_TYPE_ID";
    /**
     * Database foreign key on table TRANSACTION column MERCHANT_ID.
     */
    public static final String FK_TRANSACTION_MERCHANT_ID = "FK_TRANS_MERCHANT_ID";
    /**
     * Database index on table TRANSACTION column REFERENCE_ID.
     */
    public static final String FK_IDX_TRANS_REF_ID = "FK_IDX_TRANS_REF_ID";
    /**
     * Database index on table USER_LOGIN column USERNAME.
     */
    public static final String IDX_USERLOGIN_USERNAME = "IDX_USERLOGIN_USERNAME";

    /**
     * Constant for security role ADMIN.
     */
    public static final String ROLE_ADMIN = "ROLE_" + RoleType.TYPE.ADMIN;
    /**
     * Constant for security role MERCHANT.
     */
    public static final String ROLE_MERCHANT = "ROLE_" + RoleType.TYPE.MERCHANT;

    /**
     * Constant for length of properties and columns containing a name.
     */
    public static final int LENGTH_NAME = 100;
    /**
     * Constant for length of properties and columns containing an email address.
     */
    public static final int LENGTH_EMAIL = 200;
    /**
     * Constant for length of properties and columns containing a phone number.
     */
    public static final int LENGTH_PHONE = 50;
    /**
     * Constant for length of properties and columns containing a hashed password.
     */
    public static final int LENGTH_HASHED_PASSWORD = 100;
    /**
     * Constant for length of properties and columns containing an error reason.
     */
    public static final int LENGTH_ERROR_REASON = 200;
    /**
     * Constant for length of properties and columns containing a business identifier of a legal entity.
     */
    public static final int LENGTH_BUSINESS_IDENTIFIER = 20;
    /**
     * Constant for length of properties and columns containing a reference ID.
     */
    public static final int LENGTH_REFERENCE_ID = 40;
    /**
     * Constant for length of properties and columns containing an ID of a nomenclature entity/table.
     */
    public static final int LENGTH_NOMENCLATURE_ID = 10;
    /**
     * Constant for length of properties and columns containing a name of a nomenclature entity..
     */
    public static final int LENGTH_NOMENCLATURE_NAME = 50;
    /**
     * Constant for max integer digits in a decimal number.
     */
    public static final int MAX_DECIMAL_INTEGER_DIGITS = 65;
    /**
     * Constant for max fractional digits in a decimal number.
     */
    public static final int MAX_DECIMAL_FRACTIONAL_DIGITS = 2;
    /**
     * Constant for the initial value of a database entity sequence.
     */
    public static final int ENTITY_SEQUENCE_INITIAL_VALUE = 1000;
    /**
     * Constant for the precision of a timestamp.
     */
    public static final int TIMESTAMP_PRECISION = 3;
}
