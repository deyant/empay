package com.example.empay.service.transaction.impl;

import com.example.empay.controller.search.SearchCriteria;
import com.example.empay.controller.search.SearchRequest;
import com.example.empay.dto.transaction.TransactionCreateRequest;
import com.example.empay.dto.transaction.TransactionDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.entity.transaction.TransactionStatusType;
import com.example.empay.entity.transaction.TransactionType;
import com.example.empay.exception.TransactionValidationException;
import com.example.empay.repository.merchant.MerchantRepository;
import com.example.empay.repository.transaction.TransactionRepository;
import com.example.empay.repository.transaction.TransactionStatusTypeRepository;
import com.example.empay.repository.transaction.TransactionTypeRepository;
import com.example.empay.util.TestUtil;
import jakarta.persistence.EntityManager;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    MerchantRepository merchantRepository;

    @Mock
    TransactionTypeRepository transactionTypeRepository;

    @Mock
    TransactionStatusTypeRepository transactionStatusTypeRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Mock
    EntityManager entityManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Get by ID successful")
    @Test
    public void getByIdSuccess() {
        Transaction transaction = TestUtil.createTransactionInstance();
        transaction.setErrorReason("Error reason");

        Mockito.when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Optional<TransactionDto> transactionDto = transactionService.getById(transaction.getId());

        Assertions.assertTrue(transactionDto.isPresent());
        Assertions.assertEquals(transaction.getId(), transactionDto.get().getId());
        Assertions.assertEquals(transaction.getType().getId(), transactionDto.get().getTypeId());
        Assertions.assertEquals(transaction.getStatus().getId(), transactionDto.get().getStatus().getId());
        Assertions.assertEquals(transaction.getMerchant().getId(), transactionDto.get().getMerchantId());
        Assertions.assertEquals(transaction.getMerchant().getName(), transactionDto.get().getMerchantName());
        Assertions.assertEquals(transaction.getAmount(), transactionDto.get().getAmount());
        Assertions.assertEquals(transaction.getCustomerEmail(), transactionDto.get().getCustomerEmail());
        Assertions.assertEquals(transaction.getCustomerPhone(), transactionDto.get().getCustomerPhone());
        Assertions.assertEquals(transaction.getErrorReason(), transactionDto.get().getErrorReason());
        Assertions.assertEquals(transaction.getReferenceId(), transactionDto.get().getReferenceId());
        Assertions.assertEquals(transaction.getLastModifiedDate(), transactionDto.get().getLastModifiedDate());
        Assertions.assertEquals(transaction.getCreatedDate(), transactionDto.get().getCreatedDate());
    }

    @DisplayName("Get by ID not found")
    @Test
    public void getByIdNotFound() {
        Mockito.when(transactionRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        Optional<TransactionDto> transactionDto = transactionService.getById(UUID.randomUUID());

        Assertions.assertFalse(transactionDto.isPresent());
    }

    @DisplayName("Search by amount between 10 and 20")
    @Test
    public void searchByAmountBetween() {
        Transaction transaction = TestUtil.createTransactionInstance();
        List resultList = Collections.singletonList(transaction);
        Page page = new PageImpl(resultList);

        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setDataOption("all");
        searchRequest.setSort("statusTypeId");
        searchRequest.setAscending(true);
        SearchCriteria searchCriteria = new SearchCriteria("amount", "bt", new BigDecimal(10), new BigDecimal(20));
        searchRequest.setSearchCriteriaList(Collections.singletonList(searchCriteria));

        Page<TransactionDto> pageResult = transactionService.findBySearchCriteria(searchRequest, 10, 0, 1L);

        Assertions.assertEquals(1, pageResult.getTotalElements());
        Assertions.assertEquals(1, pageResult.getTotalPages());
        TransactionDto firstResult = pageResult.getContent().get(0);
        Assertions.assertEquals(transaction.getId(), firstResult.getId());
    }

    @DisplayName("Search without criteria")
    @Test
    public void searchWithoutCriteria() {
        Transaction transaction = TestUtil.createTransactionInstance();
        List resultList = Collections.singletonList(transaction);
        Page page = new PageImpl(resultList);

        Mockito.when(transactionRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setSort("statusTypeId");
        searchRequest.setAscending(false);

        Page<TransactionDto> pageResult = transactionService.findBySearchCriteria(searchRequest, 10, 0, 1L);

        Assertions.assertEquals(1, pageResult.getTotalElements());
        Assertions.assertEquals(1, pageResult.getTotalPages());
        TransactionDto firstResult = pageResult.getContent().get(0);
        Assertions.assertEquals(transaction.getId(), firstResult.getId());
    }

    @DisplayName("Create transaction for inactive merchant")
    @Test
    public void createTransactionForInactiveMerchant() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.INACTIVE.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.AUTHORIZE.name())
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("123123123");

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.saveAndFlush(Mockito.any(Transaction.class)))
                .thenAnswer(mock -> {
                    Transaction t = (Transaction) mock.getArguments()[0];
                    t.setId(UUID.randomUUID());
                    t.setCreatedDate(ZonedDateTime.now());
                    return t;
                });

        TransactionDto createdTransactionDto = transactionService.add(createRequest, merchant.getId());

        Assertions.assertNotNull(createdTransactionDto.getId());
        Assertions.assertNotNull(createdTransactionDto.getCreatedDate());
        Assertions.assertEquals(createRequest.getAmount(), createdTransactionDto.getAmount());
        Assertions.assertEquals(createRequest.getTypeId(), createdTransactionDto.getTypeId());
        Assertions.assertEquals(createRequest.getCustomerEmail(), createdTransactionDto.getCustomerEmail());
        Assertions.assertEquals(createRequest.getCustomerPhone(), createdTransactionDto.getCustomerPhone());
        Assertions.assertEquals(merchant.getId(), createdTransactionDto.getMerchantId());
        Assertions.assertEquals(merchant.getName(), createdTransactionDto.getMerchantName());
        Assertions.assertEquals(TransactionStatusType.TYPE.ERROR.name(), createdTransactionDto.getStatus().getId());
        Assertions.assertEquals(TransactionServiceImpl.ERROR_MESSAGE_MERCHANT_NOT_ACTIVE,
                createdTransactionDto.getErrorReason());
    }

    @DisplayName("Create transaction for non-existing merchant")
    @Test
    public void createTransactionForNonExistingMerchant() {
        Mockito.when(merchantRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.AUTHORIZE.name())
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("123123123");

        TransactionValidationException exception =
                Assertions.assertThrows(TransactionValidationException.class, () ->
                        transactionService.add(createRequest, 1L));

        Assertions.assertTrue(exception.getMessage().contains("Merchant"));
        Assertions.assertTrue(exception.getMessage().contains("does not exist"));
    }
}
