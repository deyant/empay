package com.example.empay.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.internal.engine.path.PathImpl;

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
}
