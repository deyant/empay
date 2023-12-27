package com.example.empay.dto.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TransactionStatusTypeDto {

    /**
     * Transaction status type ID.
     */
    private String id;

    /**
     * Transaction status type name.
     */
    private String name;
}
