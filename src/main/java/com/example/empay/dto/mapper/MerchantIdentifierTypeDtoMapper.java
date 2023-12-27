package com.example.empay.dto.mapper;

import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;
import com.example.empay.entity.merchant.MerchantIdentifierType;

/**
 * A mapper between {@link MerchantIdentifierTypeDto} and {@link MerchantIdentifierType} entity.
 */
public final class MerchantIdentifierTypeDtoMapper {
    /**
     * Hidden constructor.
     */
    private MerchantIdentifierTypeDtoMapper() {
    }

    /**
     * Create a new {@link MerchantIdentifierTypeDto} using a {@link MerchantIdentifierType} instance.
     *
     * @param identifierType A MerchantIdentifierType entity instance to copy the values from.
     * @return A new MerchantIdentifierTypeDto instance containing relevant values from the entity.
     */
    public static MerchantIdentifierTypeDto toDto(final MerchantIdentifierType identifierType) {
        return new MerchantIdentifierTypeDto()
                .setId(identifierType.getId())
                .setName(identifierType.getName());
    }
}
