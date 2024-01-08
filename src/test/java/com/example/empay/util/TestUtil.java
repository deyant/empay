package com.example.empay.util;

import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantIdentifierType;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.entity.transaction.TransactionStatusType;
import com.example.empay.entity.transaction.TransactionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Utilities for testing.
 */
public class TestUtil {


    private TestUtil() {
    }

    /**
     * Utility that creates a ConstraintViolation instance.
     *
     * @param message      Message to use
     * @param invalidValue Invalid value
     * @param propertyPath Property path
     * @return A {@link ConstraintViolation} instance
     */
    public static ConstraintViolation createConstraintViolation(final String message,
                                                                final String invalidValue,
                                                                final String propertyPath) {
        return new ConstraintViolation() {
            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getMessageTemplate() {
                return null;
            }

            @Override
            public Object getRootBean() {
                return null;
            }

            @Override
            public Class getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return PathImpl.createPathFromString(propertyPath);
            }

            @Override
            public Object getInvalidValue() {
                return invalidValue;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public Object unwrap(Class type) {
                return null;
            }
        };
    }

    public static Transaction createTransactionInstance() {
        return (Transaction) new Transaction()
                .setId(UUID.randomUUID())
                .setType(new TransactionType().setId(TransactionType.TYPE.CHARGE.toString()))
                .setStatus(new TransactionStatusType().setId(TransactionStatusType.TYPE.APPROVED.toString()))
                .setAmount(new BigDecimal(10.23))
                .setMerchant(new Merchant().setId(1L).setName("Test Merchant"))
                .setReferenceId("123123")
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("112233")
                .setCreatedDate(ZonedDateTime.now())
                .setLastModifiedDate(ZonedDateTime.now());
    }

    public static Merchant createMerchantInstance() {
        return new Merchant()
                .setId(1L)
                .setName("Test merchant")
                .setEmail("merchant@nosuchemail.com")
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()))
                .setIdentifierType(new MerchantIdentifierType().setId("EIK_BG"))
                .setIdentifierValue("123");
    }
}
