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
public class TransactionServiceRefundTests {

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

    @DisplayName("Create REFUND transaction success")
    @Test
    public void createRefundSuccess() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction chargeTransaction = TestUtil.createTransactionInstance();

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setCustomerEmail("test@nosuchemail.com")
                .setCustomerPhone("123123123")
                .setBelongsToTransactionId(chargeTransaction.getId());

        Mockito.when(transactionRepository.lockById(chargeTransaction.getId()))
                .thenReturn(Optional.of(chargeTransaction));

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
        Mockito.when(merchantRepository.lockById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(merchantRepository.save(Mockito.any(Merchant.class))).thenAnswer(mock -> mock.getArguments()[0]);
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.APPROVED.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.APPROVED.name()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.REFUNDED.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.REFUNDED.name()));

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

    @DisplayName("Create REFUND of a non-existing CHARGE transaction")
    @Test
    public void createRefundOfNonExistingChargeTransaction() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        UUID chargeTransactionId = UUID.randomUUID();
        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REFUND.name())
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

    @DisplayName("Create REFUND of a CHARGE transaction of another merchant")
    @Test
    public void createRefundOfChargeTransactionAnotherMerchant() {
        Merchant otherMerchant = new Merchant()
                .setId(2L)
                .setName("Other Merchant")
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction chargeTransaction = TestUtil.createTransactionInstance();
        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setBelongsToTransactionId(chargeTransaction.getId());

        Mockito.when(merchantRepository.findById(otherMerchant.getId())).thenReturn(Optional.of(otherMerchant));
        Mockito.when(transactionRepository.lockById(chargeTransaction.getId()))
                .thenReturn(Optional.of(chargeTransaction));

        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        TransactionValidationException exception =
                Assertions.assertThrows(TransactionValidationException.class, () ->
                        transactionService.add(createRequest, otherMerchant.getId()));

        Assertions.assertEquals(TransactionServiceImpl.ERROR_MESSAGE_TRANSACTION_BELONGS_ANOTHER_MERCHANT,
                exception.getMessage());
    }

    @DisplayName("Failed REFUND due to pessimistic lock merchant not found")
    @Test
    public void failedRefundPessimisticLockMerchantNotFound() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction chargeTransaction = TestUtil.createTransactionInstance();
        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setBelongsToTransactionId(chargeTransaction.getId());

        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionRepository.lockById(chargeTransaction.getId()))
                .thenReturn(Optional.of(chargeTransaction));

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(merchantRepository.lockById(merchant.getId())).thenReturn(Optional.empty());

        TransactionValidationException exception =
                Assertions.assertThrows(TransactionValidationException.class, () ->
                        transactionService.add(createRequest, merchant.getId()));

        Assertions.assertTrue(exception.getMessage().contains("Merchant"));
        Assertions.assertTrue(exception.getMessage().contains("does not exist"));
    }

    @DisplayName("Create REFUND of a transaction of a wrong type")
    @Test
    public void createRefundOfTransactionWrongType() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction nonChargeTransaction = TestUtil.createTransactionInstance();
        nonChargeTransaction.setType(new TransactionType().setId(TransactionType.TYPE.AUTHORIZE.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setBelongsToTransactionId(nonChargeTransaction.getId());

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.lockById(nonChargeTransaction.getId()))
                .thenReturn(Optional.of(nonChargeTransaction));

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
        Assertions.assertEquals(TransactionServiceImpl.ERROR_REASON_CANNOT_REFUND_TRANSACTION_OF_TYPE
                        + nonChargeTransaction.getType().getId(),
                createdTransactionDto.getErrorReason());
    }

    @DisplayName("Create REFUND of a CHARGE transaction in wrong status")
    @Test
    public void createRefundOfTransactionWrongStatus() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction chargeTransaction = TestUtil.createTransactionInstance();
        chargeTransaction.setStatus(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(10.12))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setBelongsToTransactionId(chargeTransaction.getId());

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.lockById(chargeTransaction.getId()))
                .thenReturn(Optional.of(chargeTransaction));

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
        Assertions.assertEquals(TransactionServiceImpl.ERROR_REASON_CANNOT_REFUND_CHARGE_TRANSACTION_IN_STATUS
                        + chargeTransaction.getStatus().getId(),
                createdTransactionDto.getErrorReason());
    }

    @DisplayName("Create REFUND of a transaction with greater amount than charged")
    @Test
    public void createRefundWithGreaterAmountThanCharged() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(1000.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction chargeTransaction = TestUtil.createTransactionInstance();
        chargeTransaction.setAmount(new BigDecimal(10));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(500))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setBelongsToTransactionId(chargeTransaction.getId());

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.lockById(chargeTransaction.getId()))
                .thenReturn(Optional.of(chargeTransaction));

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
        Assertions.assertEquals(TransactionServiceImpl.ERROR_REASON_REFUND_TRANSACTION_AMOUNT_GREATER_THAN_CHARGED,
                createdTransactionDto.getErrorReason());
    }

    @DisplayName("Create REFUND of a transaction with greater amount than merchant's total sum")
    @Test
    public void createRefundWithGreaterAmountMerchantTotalSum() {
        Merchant merchant = new Merchant()
                .setId(1L)
                .setName("Test Merchant")
                .setTotalTransactionSum(new BigDecimal(10.00))
                .setStatus(new MerchantStatusType().setId(MerchantStatusType.STATUS.ACTIVE.name()));

        Transaction chargeTransaction = TestUtil.createTransactionInstance();
        chargeTransaction.setAmount(new BigDecimal(100));

        TransactionCreateRequest createRequest = new TransactionCreateRequest()
                .setAmount(new BigDecimal(100))
                .setTypeId(TransactionType.TYPE.REFUND.name())
                .setBelongsToTransactionId(chargeTransaction.getId());

        Mockito.when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(merchantRepository.lockById(merchant.getId())).thenReturn(Optional.of(merchant));
        Mockito.when(transactionTypeRepository.getReferenceById(createRequest.getTypeId()))
                .thenReturn(new TransactionType().setId(createRequest.getTypeId()));

        Mockito.when(transactionStatusTypeRepository.getReferenceById(TransactionStatusType.TYPE.ERROR.name()))
                .thenReturn(new TransactionStatusType().setId(TransactionStatusType.TYPE.ERROR.name()));

        Mockito.when(transactionRepository.lockById(chargeTransaction.getId()))
                .thenReturn(Optional.of(chargeTransaction));

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
                TransactionServiceImpl.ERROR_REASON_REFUND_TRANSACTION_AMOUNT_GREATER_THAN_MERCHANT_TOTAL_SUM,
                createdTransactionDto.getErrorReason());
    }
}
