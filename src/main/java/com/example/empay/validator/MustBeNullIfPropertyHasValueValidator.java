package com.example.empay.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

/**
 * Validator for the {@link MustBeNullIfPropertyHasValue} constraint.
 */
public final class MustBeNullIfPropertyHasValueValidator
        implements ConstraintValidator<MustBeNullIfPropertyHasValue, Object> {

    /**
     * The name of the property that must be {@literal null}.
     */
    private String nullProperty;
    /**
     * The name of the property which value will be compared against a list of known values.
     */
    private String checkProperty;
    /**
     * The list of known values to which the value  of the {@link #checkProperty} will be compared.
     */
    private String[] checkPropertyValues;


    @Override
    public void initialize(@SuppressWarnings("unused") final MustBeNullIfPropertyHasValue constraintAnnotation) {

        nullProperty = constraintAnnotation.nullProperty();
        checkProperty = constraintAnnotation.checkProperty();
        checkPropertyValues = Objects.requireNonNull(constraintAnnotation.checkPropertyValues(),
                "Attribute [checkPropertyValues] must not be null.");
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

        BeanWrapper bean = new BeanWrapperImpl(value);
        Object requiredValue = bean.getPropertyValue(nullProperty);
        if (requiredValue == null) {
            return true;
        }

        Object checkValue = bean.getPropertyValue(checkProperty);

        for (String nextVal : checkPropertyValues) {
            if (checkValue.equals(nextVal)) {
                return false;
            }
        }

        return true;
    }
}
