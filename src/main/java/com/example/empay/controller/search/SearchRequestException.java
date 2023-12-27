package com.example.empay.controller.search;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SearchRequestException extends RuntimeException {

    /**
     * The search criteria that caused this exception.
     */
    @Getter
    private SearchCriteria searchCriteria;

    /**
     * Sole constructor.
     *
     * @param message        Exception message.
     * @param searchCriteria The search criteria that caused this exception.
     */
    public SearchRequestException(final String message, final SearchCriteria searchCriteria) {
        super(message);
        this.searchCriteria = searchCriteria;
    }
}
