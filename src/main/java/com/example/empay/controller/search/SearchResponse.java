package com.example.empay.controller.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchResponse<T> {

    /**
     * List of search results.
     */
    private List<T> data;
    /**
     * Number of total elements returned by the query.
     */
    private Long totalElements;
    /**
     * Number of pages.
     */
    private Integer totalPages;
}
