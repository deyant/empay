package com.example.empay.controller.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search criteria used to construct JPA (database) search query.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    /**
     * The name of the property to search on.
     */
    private String filterKey;
    /**
     * The search value.
     */
    private Object value;
    /**
     * Optional search value 2, used in cases like BETWEEN.
     */
    private Object value2;
    /**
     * The search operation as submitted by the client.
     */
    private String operation;
    /**
     * Data option describing how to combine the search criteria, either ALL or ANY.
     */
    private String dataOption;

    /**
     * Sole constructor.
     *
     * @param filterKey The filter key of the operation, usually corresponds to a property of an entity.
     * @param operation Search operation.
     * @param value     Search value.
     * @param value2    Search value 2 (optional).
     */
    public SearchCriteria(final String filterKey, final String operation,
                          final Object value, final Object value2) {
        this.filterKey = filterKey;
        this.operation = operation;
        this.value = value;
        this.value2 = value2;
    }
}
