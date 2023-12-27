package com.example.empay.service.merchant;

import com.example.empay.dto.mapper.MerchantIdentifierTypeDtoMapper;
import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;
import com.example.empay.repository.merchant.MerchantIdentifierTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class MerchantIdentifierTypeService {

    /**
     * MerchantIdentifierType repository.
     */
    @Autowired
    private MerchantIdentifierTypeRepository repository;

    /**
     * Return a list of all existing {@code MerchantIdentifierType} records wrapped as {@code
     * MerchantIdentifierTypeDto}.
     *
     * @return A list of all existing merchant identifier types.
     */
    public Collection<MerchantIdentifierTypeDto> findAll() {
        List<MerchantIdentifierTypeDto> list = new LinkedList<>();
        repository.findAll().forEach(it -> list.add(MerchantIdentifierTypeDtoMapper.toDto(it)));
        return list;
    }

}
