package com.example.empay.service.transaction.impl;

import com.example.empay.dto.mapper.TransactionTypeDtoMapper;
import com.example.empay.dto.transaction.TransactionTypeDto;
import com.example.empay.repository.transaction.TransactionTypeRepository;
import com.example.empay.service.transaction.TransactionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class TransactionTypeServiceImpl implements TransactionTypeService {

    /**
     * TransactionType repository.
     */
    @Autowired
    private TransactionTypeRepository repository;

    /**
     * Return a list of all existing {@code TransactionType} records wrapped as {@code
     * TransactionTypeDto}.
     *
     * @return A list of all existing transaction types.
     */
    public Collection<TransactionTypeDto> findAll() {
        List<TransactionTypeDto> list = new LinkedList<>();
        repository.findAll().forEach(it -> list.add(TransactionTypeDtoMapper.toDto(it)));
        return list;
    }
}
