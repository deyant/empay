package com.example.empay.dto.mapper;

import com.example.empay.dto.transaction.TransactionDto;
import com.example.empay.dto.transaction.TransactionStatusTypeDto;
import com.example.empay.entity.transaction.Transaction;

/**
 * A mapper between {@link TransactionDto} and {@link Transaction} entity.
 */
public final class TransactionDtoMapper {

    /**
     * Hidden constructor.
     */
    private TransactionDtoMapper() {
    }

    /**
     * Create a new {@link TransactionDto} using a {@link Transaction} instance.
     *
     * @param transaction A Transaction entity instance to copy the values from.
     * @return A new TransactionDto instance containing relevant values from the entity.
     */
    public static TransactionDto toDto(final Transaction transaction) {
        TransactionDto dto = new TransactionDto()
                .setId(transaction.getId())
                .setTypeId(transaction.getType().getId())
                .setType(TransactionTypeDtoMapper.toDto(transaction.getType()))
                .setMerchantId(transaction.getMerchant().getId())
                .setMerchantName(transaction.getMerchant().getName())
                .setAmount(transaction.getAmount())
                .setCustomerEmail(transaction.getCustomerEmail())
                .setCustomerPhone(transaction.getCustomerPhone())
                .setStatus(new TransactionStatusTypeDto().setId(transaction.getStatus().getId())
                        .setName(transaction.getStatus().getName()))
                .setErrorReason(transaction.getErrorReason())
                .setReferenceId(transaction.getReferenceId())
                .setCreatedDate(transaction.getCreatedDate())
                .setLastModifiedDate(transaction.getLastModifiedDate())
                .setCreatedBy(
                        transaction.getCreatedBy() != null ? UserDtoMapper.toDto(transaction.getCreatedBy()) : null)
                .setLastModifiedBy(
                        transaction.getLastModifiedBy() != null ? UserDtoMapper.toDto(transaction.getLastModifiedBy())
                                : null)
                .setVersion(transaction.getVersion());

        if (transaction.getBelongsToTransaction() != null) {
            dto.setBelongsToTransaction(TransactionDtoMapper.toDto(transaction.getBelongsToTransaction()));
        }

        return dto;
    }
}
