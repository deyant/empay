package com.example.empay.dto.mapper;

import com.example.empay.dto.merchant.MerchantStatusTypeDto;
import com.example.empay.entity.merchant.MerchantStatusType;

/**
 * A mapper between {@link MerchantStatusTypeDto} and {@link MerchantStatusType} entity.
 */
public final class MerchantStatusTypeDtoMapper {
    /**
     * Hidden constructor.
     */
    private MerchantStatusTypeDtoMapper() {
    }

    /**
     * Create a new {@link MerchantStatusTypeDto} using a {@link MerchantStatusType} instance.
     *
     * @param statusType A MerchantStatusType entity instance to copy the values from.
     * @return A new MerchantStatusTypeDto instance containing relevant values from the entity.
     */
    public static MerchantStatusTypeDto toDto(final MerchantStatusType statusType) {
        return new MerchantStatusTypeDto()
                .setId(statusType.getId())
                .setName(statusType.getName());
    }
}
