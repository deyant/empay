package com.example.empay.validator;

import com.example.empay.dto.transaction.TransactionCreateRequest;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated class contains a property specified in {@link #nullProperty()} that must be {@literal null}
 * if another property specified in {@link #checkProperty()} is not {@literal null} AND has a value any of the values
 * specified in {@link #checkPropertyValues()}.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MustBeNullIfPropertyHasValueValidator.class)
public @interface MustBeNullIfPropertyHasValue {

    /**
     * Constraints.
     *
     * @return the constraints value.
     */
    Class<?>[] constraints() default {};

    /**
     * Validation groups.
     *
     * @return The validation groups value.
     */
    Class<?>[] groups() default {};

    /**
     * Validation message, either a fixed text or a message key.
     *
     * @return The validation message.
     */
    String message() default "{validator.mustBeNullIfPropertyHasValue.message}";

    /**
     * The payload object.
     *
     * @return payload object.
     */
    Class<? extends TransactionCreateRequest>[] payload() default {};

    /**
     * The name of the property that is required to be {@literal null} if the conditions are met.
     *
     * @return Name of a property.
     */
    String nullProperty();

    /**
     * The name of the property which value will be checked against a list of values.
     *
     * @return Name of a property.
     */
    String checkProperty();

    /**
     * List of values, one of which should match the value of the {@link #checkProperty()} so that the condition is met.
     *
     * @return List of values.
     */
    String[] checkPropertyValues();
}
