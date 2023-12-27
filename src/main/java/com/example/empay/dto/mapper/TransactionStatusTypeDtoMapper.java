package com.example.empay.dto.mapper;

import com.example.empay.dto.transaction.TransactionStatusTypeDto;
import com.example.empay.entity.transaction.TransactionStatusType;

/**
 * A mapper between {@link TransactionStatusTypeDto} and {@link TransactionStatusType} entity.
 */
public final class TransactionStatusTypeDtoMapper {

    /**
     * Hidden constructor.
     */
    private TransactionStatusTypeDtoMapper() {
    }

    /**
     * Create a new {@link TransactionStatusTypeDto} using a {@link TransactionStatusType} instance.
     *
     * @param transactionStatusType A TransactionStatusType entity instance to copy the values from.
     * @return A new TransactionStatusTypeDto instance containing relevant values from the entity.
     */
    public static TransactionStatusTypeDto toDto(final TransactionStatusType transactionStatusType) {
        return new TransactionStatusTypeDto()
                .setId(transactionStatusType.getId())
                .setName(transactionStatusType.getName());
    }
}
