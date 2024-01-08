package com.example.empay.service.merchant;

import com.example.empay.dto.merchant.MerchantStatusTypeDto;

import java.util.Collection;

public interface MerchantStatusTypeService {
    /**
     * Return a list of all existing {@code MerchantStatusType} records wrapped as {@code
     * MerchantStatusTypeDto}.
     *
     * @return A list of all existing merchant status types.
     */
    Collection<MerchantStatusTypeDto> findAll();
}
