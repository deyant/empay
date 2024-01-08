package com.example.empay.service.merchant.impl;

import com.example.empay.controller.search.SearchCriteria;
import com.example.empay.controller.search.SearchRequest;
import com.example.empay.dto.mapper.MerchantDtoMapper;
import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantIdentifierType;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.repository.merchant.MerchantRepository;
import com.example.empay.util.TestUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MerchantServiceTests {

    @Mock
    MerchantRepository merchantRepository;

    @InjectMocks
    MerchantServiceImpl merchantService;

    @Mock
    EntityManager entityManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Get by ID successful")
    @Test
    public void getByIdSuccess() {
        Merchant merchant = TestUtil.createMerchantInstance();

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));

        Optional<MerchantDto> merchantDto = merchantService.getById(merchant.getId());

        Assertions.assertTrue(merchantDto.isPresent());
        Assertions.assertEquals(merchant.getId(), merchantDto.get().getId());
        Assertions.assertEquals(merchant.getName(), merchantDto.get().getName());
        Assertions.assertEquals(merchant.getStatus().getId(), merchantDto.get().getStatus().getId());
        Assertions.assertEquals(merchant.getEmail(), merchantDto.get().getEmail());
        Assertions.assertEquals(merchant.getTotalTransactionSum(), merchantDto.get().getTotalTransactionSum());
        Assertions.assertEquals(merchant.getIdentifierType().getId(), merchantDto.get().getIdentifierType().getId());
        Assertions.assertEquals(merchant.getIdentifierValue(), merchantDto.get().getIdentifierValue());
    }

    @DisplayName("Get by ID not found")
    @Test
    public void getByIdNotFound() {
        Mockito.when(merchantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        Optional<MerchantDto> merchantDto = merchantService.getById(2L);

        Assertions.assertFalse(merchantDto.isPresent());
    }

    @DisplayName("Search by email contains")
    @Test
    public void searchByEmailContains() {
        Merchant merchant = TestUtil.createMerchantInstance();
        List resultList = Collections.singletonList(merchant);
        Page page = new PageImpl(resultList);

        Mockito.when(merchantRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setDataOption("all");
        searchRequest.setSort("statusTypeId");
        searchRequest.setAscending(true);
        SearchCriteria searchCriteria = new SearchCriteria("email", "cn", "@nosuchemail", null);
        searchRequest.setSearchCriteriaList(Collections.singletonList(searchCriteria));

        Page<MerchantDto> pageResult = merchantService.findBySearchCriteria(searchRequest, 10, 0, 1L);

        Assertions.assertEquals(1, pageResult.getTotalElements());
        Assertions.assertEquals(1, pageResult.getTotalPages());
        MerchantDto firstResult = pageResult.getContent().get(0);
        Assertions.assertEquals(merchant.getId(), firstResult.getId());
    }

    @DisplayName("Search without criteria")
    @Test
    public void searchWithoutCriteria() {
        Merchant merchant = TestUtil.createMerchantInstance();
        List resultList = Collections.singletonList(merchant);
        Page page = new PageImpl(resultList);

        Mockito.when(merchantRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setSort("statusTypeId");
        searchRequest.setAscending(false);

        Page<MerchantDto> pageResult = merchantService.findBySearchCriteria(searchRequest, 10, 0, 1L);

        Assertions.assertEquals(1, pageResult.getTotalElements());
        Assertions.assertEquals(1, pageResult.getTotalPages());
        MerchantDto firstResult = pageResult.getContent().get(0);
        Assertions.assertEquals(merchant.getId(), firstResult.getId());
    }

    @DisplayName("Create merchant successful")
    @Test
    public void createMerchant() {
        Merchant merchant = TestUtil.createMerchantInstance();
        merchant.setId(null);

        MerchantDto merchantDto = MerchantDtoMapper.toDto(merchant);

        Mockito.when(entityManager.getReference(Mockito.eq(MerchantIdentifierType.class), Mockito.any(Object.class)))
                .thenAnswer(mock -> new MerchantIdentifierType().setId((String) mock.getArguments()[1]));

        Mockito.when(entityManager.getReference(Mockito.eq(MerchantStatusType.class), Mockito.any(Object.class)))
                .thenAnswer(mock -> new MerchantStatusType().setId((String) mock.getArguments()[1]));

        Mockito.when(merchantRepository.saveAndFlush(Mockito.any(Merchant.class)))
                .thenAnswer(mock -> {
                    Merchant m = (Merchant) mock.getArguments()[0];
                    m.setId(1L);
                    m.setCreatedDate(ZonedDateTime.now());
                    m.setVersion(1);
                    return m;
                });
        MerchantDto createdMerchantDto = merchantService.add(merchantDto);

        Assertions.assertNotNull(createdMerchantDto.getId());
        Assertions.assertNotNull(createdMerchantDto.getCreatedDate());
        Assertions.assertEquals(1, createdMerchantDto.getVersion());
        Assertions.assertEquals(merchantDto.getName(), createdMerchantDto.getName());
        Assertions.assertEquals(merchantDto.getEmail(), createdMerchantDto.getEmail());
        Assertions.assertEquals(merchantDto.getStatus().getId(), createdMerchantDto.getStatus().getId());
        Assertions.assertEquals(merchantDto.getIdentifierType().getId(),
                createdMerchantDto.getIdentifierType().getId());
        Assertions.assertEquals(merchantDto.getIdentifierValue(), createdMerchantDto.getIdentifierValue());
    }

    @DisplayName("Update merchant successful")
    @Test
    public void updateMerchant() {
        Integer initialVersion = 5;
        Merchant merchant = TestUtil.createMerchantInstance();
        merchant.setVersion(initialVersion);

        MerchantDto merchantDto = MerchantDtoMapper.toDto(merchant);
        merchantDto.setName("new name");

        Mockito.when(entityManager.getReference(Mockito.eq(MerchantIdentifierType.class), Mockito.any(Object.class)))
                .thenAnswer(mock -> new MerchantIdentifierType().setId((String) mock.getArguments()[1]));

        Mockito.when(entityManager.getReference(Mockito.eq(MerchantStatusType.class), Mockito.any(Object.class)))
                .thenAnswer(mock -> new MerchantStatusType().setId((String) mock.getArguments()[1]));

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(merchantRepository.saveAndFlush(Mockito.any(Merchant.class)))
                .thenAnswer(mock -> {
                    Merchant m = (Merchant) mock.getArguments()[0];
                    m.setId(1L);
                    m.setLastModifiedDate(ZonedDateTime.now());
                    m.setVersion(m.getVersion() + 1);
                    return m;
                });
        MerchantDto updatedMerchantDto = merchantService.update(merchant.getId(), merchantDto);

        Assertions.assertEquals(merchantDto.getId(), updatedMerchantDto.getId());
        Assertions.assertNotNull(updatedMerchantDto.getLastModifiedDate());
        Assertions.assertEquals(merchant.getCreatedDate(), updatedMerchantDto.getCreatedDate());
        Assertions.assertEquals(initialVersion + 1, updatedMerchantDto.getVersion());
        Assertions.assertEquals(merchantDto.getName(), updatedMerchantDto.getName());
        Assertions.assertEquals(merchantDto.getEmail(), updatedMerchantDto.getEmail());
        Assertions.assertEquals(merchantDto.getStatus().getId(), updatedMerchantDto.getStatus().getId());
        Assertions.assertEquals(merchantDto.getIdentifierType().getId(),
                updatedMerchantDto.getIdentifierType().getId());
        Assertions.assertEquals(merchantDto.getIdentifierValue(), updatedMerchantDto.getIdentifierValue());
    }

    @DisplayName("Update non-existing merchant")
    @Test
    public void updateNonExistingMerchant() {
        Mockito.when(merchantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        Merchant merchant = TestUtil.createMerchantInstance();
        MerchantDto merchantDto = MerchantDtoMapper.toDto(merchant);
        merchantDto.setName("new name");

        EntityNotFoundException exception =
                Assertions.assertThrows(EntityNotFoundException.class, () ->
                        merchantService.update(1L, merchantDto));

        Assertions.assertTrue(exception.getMessage().contains("Merchant"));
        Assertions.assertTrue(exception.getMessage().contains("does not exist"));
    }

    @DisplayName("Delete merchant")
    @Test
    public void deleteMerchant() {
        Merchant merchant = TestUtil.createMerchantInstance();
        Mockito.when(merchantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(merchant));

        Assertions.assertDoesNotThrow(() -> merchantService.delete(1L));
    }

    @DisplayName("Delete non-existing merchant")
    @Test
    public void deleteNonExistingMerchant() {
        Mockito.when(merchantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                Assertions.assertThrows(EntityNotFoundException.class, () ->
                        merchantService.delete(1L));

        Assertions.assertTrue(exception.getMessage().contains("Merchant"));
        Assertions.assertTrue(exception.getMessage().contains("does not exist"));
    }
}
