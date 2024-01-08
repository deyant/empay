package com.example.empay.service.merchant.impl;

import com.example.empay.dto.mapper.MerchantStatusTypeDtoMapper;
import com.example.empay.dto.merchant.MerchantStatusTypeDto;
import com.example.empay.repository.merchant.MerchantStatusTypeRepository;
import com.example.empay.service.merchant.MerchantStatusTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class MerchantStatusTypeServiceImpl implements MerchantStatusTypeService {

    /**
     * MerchantStatusType repository.
     */
    @Autowired
    private MerchantStatusTypeRepository repository;

    /**
     * Return a list of all existing {@code MerchantStatusType} records wrapped as {@code
     * MerchantStatusTypeDto}.
     *
     * @return A list of all existing merchant status types.
     */
    public Collection<MerchantStatusTypeDto> findAll() {
        List<MerchantStatusTypeDto> list = new LinkedList<>();
        repository.findAll().forEach(it -> list.add(MerchantStatusTypeDtoMapper.toDto(it)));
        return list;
    }
}
