package com.example.empay.controller.search;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Contains all possible search operations.
 */
public enum SearchOperation {

    /**
     * Contains.
     */
    CONTAINS,
    /**
     * Does not contain.
     */
    DOES_NOT_CONTAIN,
    /**
     * Equal to.
     */
    EQUAL,
    /**
     * Not equal to.
     */
    NOT_EQUAL,
    /**
     * Begins with (for a string).
     */
    BEGINS_WITH,
    /**
     * Does not begin with (for a string).
     */
    DOES_NOT_BEGIN_WITH,
    /**
     * Ends with (for a string).
     */
    ENDS_WITH,
    /**
     * Does not end with (for a string).
     */
    DOES_NOT_END_WITH,
    /**
     * NUL literal reference.
     */
    NUL,
    /**
     * Not NUL.
     */
    NOT_NULL,
    /**
     * Greater than. Applies to numbers and dates.
     */
    GREATER_THAN,
    /**
     * Greater than or equal to. Applies to numbers and dates.
     */
    GREATER_THAN_EQUAL,
    /**
     * Less than. Applies to numbers and dates.
     */
    LESS_THAN,
    /**
     * Less than or equal to. Applies to numbers and dates.
     */
    LESS_THAN_EQUAL,
    /**
     * Between. Applies to numbers and dates.
     */
    BETWEEN,
    /**
     * Data option ANY.
     */
    ANY,
    /**
     * Data option ALL.
     */
    ALL;

    /**
     * Maps user-provided search operations to enum values.
     */
    private static final Map<String, SearchOperation> SIMPLE_OPERATION_MAP = new HashMap<>() {{
        put("cn", CONTAINS);
        put("nc", DOES_NOT_CONTAIN);
        put("eq", EQUAL);
        put("ne", NOT_EQUAL);
        put("bw", BEGINS_WITH);
        put("bn", DOES_NOT_BEGIN_WITH);
        put("ew", ENDS_WITH);
        put("en", DOES_NOT_END_WITH);
        put("nu", NUL);
        put("nn", NOT_NULL);
        put("gt", GREATER_THAN);
        put("ge", GREATER_THAN_EQUAL);
        put("lt", LESS_THAN);
        put("le", LESS_THAN_EQUAL);
        put("bt", BETWEEN);
    }};


    /**
     * Get a data option from a user-provided string.
     *
     * @param dataOption The data option String, one of "all" or "any". Case sensitive.
     * @return enum value or {@literal null} if not found.
     */
    public static SearchOperation getDataOption(final String dataOption) {
        if (dataOption == null) {
            return null;
        }
        switch (dataOption) {
            case "all":
                return ALL;
            case "any":
                return ANY;
            default:
                return null;
        }
    }

    /**
     * Get a search operation enum from user-provided String.
     *
     * @param input User provided String.
     * @return Enum value or {@literal null} if not found.
     */
    public static SearchOperation getSimpleOperation(final String input) {
        Objects.requireNonNull(input, "Argument [input] cannot be null");
        return SIMPLE_OPERATION_MAP.get(input.toLowerCase());
    }
}
