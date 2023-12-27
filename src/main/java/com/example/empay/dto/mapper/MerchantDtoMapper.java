package com.example.empay.dto.mapper;

import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantIdentifierType;
import com.example.empay.entity.merchant.MerchantStatusType;
import jakarta.persistence.EntityManager;

/**
 * A mapper between {@link MerchantDto} and {@link Merchant} entity.
 */
public final class MerchantDtoMapper {

    /**
     * Hidden constructor.
     */
    private MerchantDtoMapper() { }

    /**
     * Create a new {@link MerchantDto} using a {@link Merchant} instance.
     *
     * @param merchant A Merchant entity instance to copy the values from.
     * @return A new MerchantDto instance containing relevant values from the entity.
     */
    public static MerchantDto toDto(final Merchant merchant) {
        return new MerchantDto()
                .setId(merchant.getId())
                .setName(merchant.getName())
                .setEmail(merchant.getEmail())
                .setStatus(MerchantStatusTypeDtoMapper.toDto(merchant.getStatus()))
                .setIdentifierType(merchant.getIdentifierType() != null
                        ? MerchantIdentifierTypeDtoMapper.toDto(merchant.getIdentifierType()) : null)
                .setIdentifierValue(merchant.getIdentifierValue())
                .setTotalTransactionSum(merchant.getTotalTransactionSum())
                .setCreatedDate(merchant.getCreatedDate())
                .setLastModifiedDate(merchant.getLastModifiedDate())
                .setCreatedBy(merchant.getCreatedBy() != null ? UserDtoMapper.toDto(merchant.getCreatedBy()) : null)
                .setLastModifiedBy(
                        merchant.getLastModifiedBy() != null ? UserDtoMapper.toDto(merchant.getLastModifiedBy()) : null)
                .setVersion(merchant.getVersion());
    }

    /**
     * Copy relevant values from a provided {@ link MerchantDto} instance to a {@link Merchant} instance.
     * @param dto The DTO instance to copy values from.
     * @param model The entity instance to copy values to.
     * @param entityManager EntityManager to use during copy.
     */
    public static void applyValuesToModel(final MerchantDto dto, final Merchant model,
                                          final EntityManager entityManager) {
        model.setName(dto.getName())
                .setEmail(dto.getEmail())
                .setVersion(dto.getVersion());

        if (dto.getIdentifierType() != null) {
            model.setIdentifierType(entityManager.getReference(MerchantIdentifierType.class,
                    dto.getIdentifierType().getId()));
        } else {
            model.setIdentifierType(null);
        }

        if (dto.getIdentifierValue() != null) {
            model.setIdentifierValue(dto.getIdentifierValue());
        } else {
            model.setIdentifierValue(null);
        }

        if (dto.getStatus() != null && dto.getStatus().getId() != null) {
            model.setStatus(entityManager.getReference(MerchantStatusType.class, dto.getStatus().getId()));
        }
    }
}
