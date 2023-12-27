package com.example.empay.dto.mapper;

import com.example.empay.dto.transaction.TransactionTypeDto;
import com.example.empay.entity.transaction.TransactionType;

/**
 * A mapper between {@link TransactionTypeDto} and {@link TransactionType} entity.
 */
public final class TransactionTypeDtoMapper {

    /**
     * Hidden constructor.
     */
    private TransactionTypeDtoMapper() {
    }

    /**
     * Create a new {@link TransactionTypeDto} using a {@link TransactionType} instance.
     *
     * @param transactionType A TransactionType entity instance to copy the values from.
     * @return A new TransactionTypeDto instance containing relevant values from the entity.
     */
    public static TransactionTypeDto toDto(final TransactionType transactionType) {
        return new TransactionTypeDto()
                .setId(transactionType.getId())
                .setName(transactionType.getName());
    }
}
