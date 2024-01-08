package com.example.empay.service.merchant;

import com.example.empay.controller.search.SearchRequest;
import com.example.empay.dto.merchant.MerchantDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * Handles merchants CRUD and search business logic.
 */
public interface MerchantService {
    /**
     * Get a merchant by ID.
     *
     * @param id The ID of the merchant.
     * @return Optional value of a {@code MerchantDto}.
     */
    Optional<MerchantDto> getById(@NotNull Long id);


    /**
     * Create a new merchant.
     *
     * @param merchantDto The merchant data.
     * @return The created merchant.
     */
    MerchantDto add(@NotNull MerchantDto merchantDto);

    /**
     * Update an existing merchant identified by ID.
     *
     * @param id          The ID of the merchant to udpate.
     * @param merchantDto The new data of the merchant.
     * @return The updated merchant.
     */
    MerchantDto update(@NotNull Long id, @NotNull MerchantDto merchantDto) throws EntityNotFoundException;

    /**
     * Delete an existing merchant identified by ID.
     *
     * @param id The ID of the merchant ot delete.
     * @throws EntityNotFoundException If no merchant with the specified ID exists.
     */
    void delete(@NotNull Long id) throws EntityNotFoundException;

    /**
     * Find merchants by a combination of search criteria.
     *
     * @param searchRequest      (optional) Contains the combination of search criteria.
     * @param pageSize           Maximum number of results in a page.
     * @param pageNum            The number of the page to return, starting from 0 (zero).
     * @param filterByMerchantId (optional) Enforce a mandatory filter by merchant ID. This will overwrite any
     *                           user-provided filters by merchant ID.
     * @return A paged result of merchants.
     */
    Page<MerchantDto> findBySearchCriteria(@Nullable SearchRequest searchRequest,
                                           @NotNull Integer pageSize,
                                           @NotNull Integer pageNum,
                                           @Nullable Long filterByMerchantId);
}
