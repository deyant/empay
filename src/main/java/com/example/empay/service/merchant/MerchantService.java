package com.example.empay.service.merchant;

import com.example.empay.controller.search.SearchCriteria;
import com.example.empay.controller.search.SearchRequest;
import com.example.empay.dto.mapper.MerchantDtoMapper;
import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.repository.merchant.MerchantRepository;
import com.example.empay.service.merchant.search.MerchantSpecification;
import com.example.empay.service.search.SpecificationBuilder;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class MerchantService {

    /**
     * Constant for ID property.
     */
    private static final String DTO_PROPERTY_ID = "id";
    /**
     * Merchant repository.
     */
    @Autowired
    private MerchantRepository repository;

    /**
     * The persistence context.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get a merchant by ID.
     *
     * @param id The ID of the merchant.
     * @return Optional value of a {@code MerchantDto}.
     */
    @Transactional
    public Optional<MerchantDto> getById(@NotNull final Long id) {
        Optional<Merchant> merchant = repository.findById(id);
        if (merchant.isPresent()) {
            return Optional.of(MerchantDtoMapper.toDto(repository.findById(id).get()));
        } else {
            return Optional.empty();
        }
    }


    /**
     * Create a new merchant.
     *
     * @param merchantDto The merchant data.
     * @return The created merchant.
     */
    @Transactional
    public MerchantDto add(@NotNull final MerchantDto merchantDto) {
        Merchant merchant = new Merchant();
        MerchantDtoMapper.applyValuesToModel(merchantDto, merchant, entityManager);
        merchantDto.setVersion(1); // Just in case if submitted by the user

        return MerchantDtoMapper.toDto(repository.saveAndFlush(merchant));
    }

    /**
     * Update an existing merchant identified by ID.
     *
     * @param id          The ID of the merchant to udpate.
     * @param merchantDto The new data of the merchant.
     * @return The updated merchant.
     */
    @Transactional
    public MerchantDto update(@NotNull final Long id, @NotNull final MerchantDto merchantDto) {
        Merchant merchant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("%s with ID [%s] does not exist.",
                        Merchant.class.getSimpleName(), id)));

        MerchantDtoMapper.applyValuesToModel(merchantDto, merchant, entityManager);
        return MerchantDtoMapper.toDto(repository.saveAndFlush(merchant));
    }

    /**
     * Delete an existing merchant identified by ID.
     *
     * @param id The ID of the merchant ot delete.
     * @throws EntityNotFoundException If no merchant with the specified ID exists.
     */
    @Transactional
    public void delete(@NotNull final Long id) {
        Merchant merchant = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("%s with ID [%s] does not exist.",
                        Merchant.class.getSimpleName(), id)));

        repository.delete(merchant);
    }


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
    public Page<MerchantDto> findBySearchCriteria(@Nullable final SearchRequest searchRequest,
                                                  @NotNull final Integer pageSize,
                                                  @NotNull final Integer pageNum,
                                                  @Nullable final Long filterByMerchantId) {

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        SpecificationBuilder<Merchant> specificationBuilder =
                new SpecificationBuilder<>(() -> new MerchantSpecification(), entityManager);

        if (searchRequest != null) {
            List<SearchCriteria> criteriaList = searchRequest.getSearchCriteriaList();
            if (filterByMerchantId != null) {
                if (criteriaList == null) {
                    criteriaList = new ArrayList<>(1);
                } else {
                    // Remove search criteria by merchantId if filterByMerchantId is provided
                    criteriaList = criteriaList.stream()
                            .filter(it -> !it.getFilterKey().equals(DTO_PROPERTY_ID))
                            .collect(Collectors.toList());
                }

                SearchCriteria merchantSearchCriteria = new SearchCriteria();
                merchantSearchCriteria.setFilterKey(DTO_PROPERTY_ID);
                merchantSearchCriteria.setOperation("eq");
                merchantSearchCriteria.setValue(filterByMerchantId);
                criteriaList.add(merchantSearchCriteria);

            }

            if (criteriaList != null) {
                criteriaList.forEach(it -> {
                    it.setDataOption(searchRequest.getDataOption());
                    specificationBuilder.with(it);
                });
            }

            if (searchRequest.getSort() != null && !searchRequest.getSort().isBlank()) {
                String sortProperty = searchRequest.getSort();
                if (sortProperty.equals("statusTypeId")) {
                    sortProperty = "status";
                }
                Sort sort = Sort.by(sortProperty);
                if (searchRequest.isAscending()) {
                    sort = sort.ascending();
                } else {
                    sort = sort.descending();
                }
                pageRequest = pageRequest.withSort(sort);
            }
        }
        Page<Merchant> searchResult = repository.findAll(specificationBuilder.build(), pageRequest);
        Page<MerchantDto> dtoPage = searchResult.map(merchant -> MerchantDtoMapper.toDto(merchant));
        return dtoPage;
    }
}
