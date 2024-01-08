package com.example.empay.service.transaction;

import com.example.empay.dto.transaction.TransactionStatusTypeDto;

import java.util.Collection;

public interface TransactionStatusTypeService {

    /**
     * Return a list of all existing {@code TransactionStatusType} records wrapped as {@code
     * TransactionStatusTypeDto}.
     *
     * @return A list of all existing transaction status types.
     */
    Collection<TransactionStatusTypeDto> findAll();
}
