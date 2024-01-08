package com.example.empay.service.merchant;

import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;

import java.util.Collection;

public interface MerchantIdentifierTypeService {
    /**
     * Return a list of all existing {@code MerchantIdentifierType} records wrapped as {@code
     * MerchantIdentifierTypeDto}.
     *
     * @return A list of all existing merchant identifier types.
     */
    Collection<MerchantIdentifierTypeDto> findAll();

}
