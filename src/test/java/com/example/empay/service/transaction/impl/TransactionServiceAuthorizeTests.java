package com.example.empay.service.transaction.impl;

import com.example.empay.dto.transaction.TransactionCreateRequest;
import com.example.empay.dto.transaction.TransactionDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.entity.transaction.TransactionStatusType;
import com.example.empay.entity.transaction.TransactionType;
import com.example.empay.repository.merchant.MerchantRepository;
import com.example.empay.repository.transaction.TransactionRepository;
import com.example.empay.repository.transaction.TransactionStatusTypeRepository;
import com.example.empay.repository.transaction.TransactionTypeRepository;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceAuthorizeTests {

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

    @DisplayName("Create AUTHORIZE transaction success")
    @Test
    public void createAuthorizeSuccess() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.AUTHORIZE.name())
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("123123123");

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.APPROVED.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.APPROVED.name()));

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
        Assertions.assertEquals(TransactionStatusType.TYPE.APPROVED.name(), createdTransactionDto.getStatus().getId());
    }
}
