package com.example.empay.service.transaction.impl;

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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceReversalTests {

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

    @DisplayName("Create REVERSAL transaction success")
    @Test
    public void createReversalSuccess() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction authorizeTransaction = TestUtil.createTransactionInstance();
        authorizeTransaction.setType(new TransactionType().setId(TransactionType.TYPE.AUTHORIZE.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(null)
                .setTypeId(TransactionType.TYPE.REVERSAL.name())
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("123123123")
                .setBelongsToTransactionId(authorizeTransaction.getId());

        Mockito.when(transactionRepository.lockById(authorizeTransaction.getId()))
                .thenReturn(Optional.of(authorizeTransaction));

        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class)))
                .thenAnswer(mock -> mock.getArguments()[0]);

        Mockito.when(transactionRepository.saveAndFlush(Mockito.any(Transaction.class)))
                .thenAnswer(mock -> {
                    Transaction t = (Transaction) mock.getArguments()[0];
                    t.setId(UUID.randomUUID());
                    t.setCreatedDate(ZonedDateTime.now());
                    return t;
                });

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.APPROVED.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.APPROVED.name()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.REVERSED.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.REVERSED.name()));

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

    @DisplayName("Create REVERSAL of a non-existing transaction")
    @Test
    public void createReversalOfNonExistingChargeTransaction() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        UUID chargeTransactionId = UUID.randomUUID();
        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REVERSAL.name())
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("123123123")
                .setBelongsToTransactionId(chargeTransactionId);

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionRepository.lockById(chargeTransactionId)).thenReturn(Optional.empty());
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        TransactionValidationException exception =
                Assertions.assertThrows(TransactionValidationException.class, () ->
                        transactionService.add(createRequest, merchant.getId()));

        Assertions.assertTrue(exception.getMessage().contains("Transaction"));
        Assertions.assertTrue(exception.getMessage().contains("does not exist"));
    }

    @DisplayName("Create REVERSAL of an AUTHORIZE transaction of another merchant")
    @Test
    public void createReversalOfAuthorizeTransactionAnotherMerchant() {
        Merchant otherMerchant = new Merchant()
                .setId(2L)
                .setName("Other Merchant")
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction authorizeTransaction = TestUtil.createTransactionInstance();
        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REVERSAL.name())
                .setBelongsToTransactionId(authorizeTransaction.getId());

        Mockito.when(merchantRepository.findById(otherMerchant.getId())).thenReturn(Optional.of(otherMerchant));
        Mockito.when(transactionRepository.lockById(authorizeTransaction.getId()))
                .thenReturn(Optional.of(authorizeTransaction));

        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        TransactionValidationException exception =
                Assertions.assertThrows(TransactionValidationException.class, () ->
                        transactionService.add(createRequest, otherMerchant.getId()));

        Assertions.assertEquals(TransactionServiceImpl.ERROR_MESSAGE_TRANSACTION_BELONGS_ANOTHER_MERCHANT,
                exception.getMessage());
    }

    @DisplayName("Create REVERSAL of a transaction of a wrong type")
    @Test
    public void createReversalOfTransactionWrongType() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction nonAuthTransaction = TestUtil.createTransactionInstance();
        nonAuthTransaction.setType(new TransactionType().setId(TransactionType.TYPE.CHARGE.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REVERSAL.name())
                .setBelongsToTransactionId(nonAuthTransaction.getId());

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.lockById(nonAuthTransaction.getId()))
                .thenReturn(Optional.of(nonAuthTransaction));

        Mockito.when(transactionRepository.saveAndFlush(Mockito.any(Transaction.class))).thenAnswer(mock -> {
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
        Assertions.assertEquals(TransactionServiceImpl.ERROR_REASON_CANNOT_REVERSE_TRANSACTION_OF_TYPE
                        + nonAuthTransaction.getType().getId(),
                createdTransactionDto.getErrorReason());
    }

    @DisplayName("Create REVERSAL of an AUTHORIZE transaction in wrong status")
    @Test
    public void createRefundOfTransactionWrongStatus() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction authorizeTransaction = TestUtil.createTransactionInstance();
        authorizeTransaction.setType(new TransactionType().setId(TransactionType.TYPE.AUTHORIZE.name()));
        authorizeTransaction.setStatus(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REVERSAL.name())
                .setBelongsToTransactionId(authorizeTransaction.getId());

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.lockById(authorizeTransaction.getId()))
                .thenReturn(Optional.of(authorizeTransaction));

        Mockito.when(transactionRepository.saveAndFlush(Mockito.any(Transaction.class))).thenAnswer(mock -> {
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
        Assertions.assertEquals(
                TransactionServiceImpl.ERROR_REASON_CANNOT_REVERSE_AUTHORIZE_TRANSACTION_IN_STATUS
                        + authorizeTransaction.getStatus().getId(),
                createdTransactionDto.getErrorReason());
    }
}
