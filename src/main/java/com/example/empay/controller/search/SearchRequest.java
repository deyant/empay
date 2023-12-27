package com.example.empay.controller.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    /**
     * List of search criteria.
     */
    private List<SearchCriteria> searchCriteriaList;
    /**
     * Data option.
     */
    private String dataOption;
    /**
     * Name of a property (field key) to sort the results by.
     */
    private String sort;
    /**
     * Denotes the direction of sort.
     */
    private boolean ascending;

}
