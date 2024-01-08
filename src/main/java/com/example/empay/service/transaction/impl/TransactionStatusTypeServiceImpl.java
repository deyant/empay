package com.example.empay.service.transaction.impl;

import com.example.empay.dto.mapper.TransactionStatusTypeDtoMapper;
import com.example.empay.dto.transaction.TransactionStatusTypeDto;
import com.example.empay.repository.transaction.TransactionStatusTypeRepository;
import com.example.empay.service.transaction.TransactionStatusTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class TransactionStatusTypeServiceImpl implements TransactionStatusTypeService {

    /**
     * TransactionStatusType repository.
     */
    @Autowired
    private TransactionStatusTypeRepository repository;

    /**
     * Return a list of all existing {@code TransactionStatusType} records wrapped as {@code
     * TransactionStatusTypeDto}.
     *
     * @return A list of all existing transaction status types.
     */
    public Collection<TransactionStatusTypeDto> findAll() {
        List<TransactionStatusTypeDto> list = new LinkedList<>();
        repository.findAll().forEach(it -> list.add(TransactionStatusTypeDtoMapper.toDto(it)));
        return list;
    }
}
