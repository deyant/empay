package com.example.empay.dto.error;

import com.example.empay.util.Constants;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Error information from execution of a HTTP request.
 */
@Getter
public class ErrorInfo {

    /**
     * The date and time when the error happened.
     */
    private ZonedDateTime timestamp;
    /**
     * The HTTP status code.
     */
    private int status;
    /**
     * Short error message.
     */
    private String error;
    /**
     * Detailed error message.
     */
    private String message;
    /**
     * The requested URI which caused the error.
     */
    private String path;

    /**
     * Constructor.
     *
     * @param httpStatus HTTP status code.
     * @param message    Detailed error message.
     * @param path       Request URI which caused the error.
     */
    public ErrorInfo(final HttpStatus httpStatus, final String message, final String path) {
        this.error = httpStatus.getReasonPhrase();
        this.status = httpStatus.value();
        this.message = message;
        this.path = path;
        this.timestamp = ZonedDateTime.now(Constants.ZONE_ID_UTC);
    }
}
