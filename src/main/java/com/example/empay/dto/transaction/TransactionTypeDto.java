package com.example.empay.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TransactionTypeDto {

    /**
     * Transaction type ID.
     */
    private String id;

    /**
     * Transaction type name.
     */
    private String name;
}
