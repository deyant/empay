package com.example.empay.dto.error;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Set;

/**
 * Error information with constraint validation errors.
 */
@Getter
public class ConstraintValidationErrorInfo extends ErrorInfo {

    /** A set of constraint violations. */
    private Set<ConstraintViolation<?>> constraintViolations;

    /**
     * The only constructor.
     * @param httpStatus HTTP status code.
     * @param message A summary error message.
     * @param path Requested URI that caused the error.
     * @param constraintViolationsSet A set of <code>ConstraintViolations</code> for each error.
     */
    public ConstraintValidationErrorInfo(final HttpStatus httpStatus, final String message, final String path,
                     final Set<ConstraintViolation<?>> constraintViolationsSet) {
        super(httpStatus, message, path);
        this.constraintViolations = constraintViolationsSet;
    }
}
