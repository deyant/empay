package com.example.empay.dto.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class TransactionDto {

    /**
     * Transaction ID.
     */
    private UUID id;
    /**
     * Transaction type DTO.
     */
    private TransactionTypeDto type;
    /**
     * Transaction type ID.
     */
    private String typeId;
    /**
     * Transaction amount.
     */
    private BigDecimal amount;
    /**
     * Customer email.
     */
    private String customerEmail;
    /**
     * Customer phone.
     */
    private String customerPhone;
    /**
     * Transaction status DTO.
     */
    private TransactionStatusTypeDto status;
    /**
     * Business reference ID.
     */
    private String referenceId;
    /**
     * Merchant ID of this transaction.
     */
    private Long merchantId;
    /**
     * Merchant name.
     */
    private String merchantName;
    /**
     * Error reason if status is ERROR.
     */
    private String errorReason;
    /**
     * Belongs to transaction DTO.
     */
    private TransactionDto belongsToTransaction;
    /**
     * Belonging transaction DTO.
     */
    private TransactionDto belongingTransaction;
    /**
     * Transaction created datetime..
     */
    private ZonedDateTime createdDate;
    /**
     * Transaction last modified datetime.
     */
    private ZonedDateTime lastModifiedDate;
    /**
     * Version number for optimistic locking.
     */
    private Integer version;
}
