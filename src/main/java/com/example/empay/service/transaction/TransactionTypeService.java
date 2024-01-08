package com.example.empay.service.transaction;

import com.example.empay.dto.transaction.TransactionTypeDto;

import java.util.Collection;

public interface TransactionTypeService {
    /**
     * Return a list of all existing {@code TransactionType} records wrapped as {@code
     * TransactionTypeDto}.
     *
     * @return A list of all existing transaction types.
     */
    Collection<TransactionTypeDto> findAll();
}
