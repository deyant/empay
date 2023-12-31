package com.example.empay.service.transaction.impl;

import com.example.empay.controller.search.SearchCriteria;
import com.example.empay.controller.search.SearchRequest;
import com.example.empay.dto.mapper.TransactionDtoMapper;
import com.example.empay.dto.transaction.TransactionCreateRequest;
import com.example.empay.dto.transaction.TransactionDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.merchant.MerchantStatusType;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.entity.transaction.TransactionType;
import com.example.empay.exception.TransactionValidationException;
import com.example.empay.repository.merchant.MerchantRepository;
import com.example.empay.repository.transaction.TransactionRepository;
import com.example.empay.repository.transaction.TransactionStatusTypeRepository;
import com.example.empay.repository.transaction.TransactionTypeRepository;
import com.example.empay.service.search.SpecificationBuilder;
import com.example.empay.service.transaction.TransactionService;
import com.example.empay.service.transaction.search.TransactionSpecification;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.empay.entity.transaction.TransactionStatusType.TYPE.APPROVED;
import static com.example.empay.entity.transaction.TransactionStatusType.TYPE.ERROR;
import static com.example.empay.entity.transaction.TransactionStatusType.TYPE.REFUNDED;
import static com.example.empay.entity.transaction.TransactionStatusType.TYPE.REVERSED;

@Service
@Validated
public class TransactionServiceImpl implements TransactionService {

    /**
     * Error message used when a related transaction belongs to another merchant.
     */
    public static final String ERROR_MESSAGE_TRANSACTION_BELONGS_ANOTHER_MERCHANT =
            "The parent transaction belongs to a different merchant.";

    /**
     * Error message used when a merchant is not active.
     */
    public static final String ERROR_MESSAGE_MERCHANT_NOT_ACTIVE = "Merchant not active.";

    /**
     * Error message used when trying to refund a charge transaction that is not in status APPROVED.
     */
    public static final String ERROR_REASON_CANNOT_REFUND_CHARGE_TRANSACTION_IN_STATUS =
            "Cannot refund a CHARGE transaction in status ";

    /**
     * Error message used when trying to reverse an authorize transaction that is not in status APPROVED.
     */
    public static final String ERROR_REASON_CANNOT_REVERSE_AUTHORIZE_TRANSACTION_IN_STATUS =
            "Cannot reverse an AUTHORIZE transaction in status ";

    /**
     * Error message used when trying to refund a transaction that is not of type CHARGE.
     */
    public static final String ERROR_REASON_CANNOT_REFUND_TRANSACTION_OF_TYPE =
            "Cannot refund a transaction of type ";

    /**
     * Error message used when trying to reverse a transaction that is not of type AUTHORIZE.
     */
    public static final String ERROR_REASON_CANNOT_REVERSE_TRANSACTION_OF_TYPE =
            "Cannot reverse a transaction of type ";

    /**
     * Error message used when trying to refund a greater amount than charged.
     */
    public static final String ERROR_REASON_REFUND_TRANSACTION_AMOUNT_GREATER_THAN_CHARGED =
            "Amount of the REFUND transaction is greater than the amount of the CHARGE transaction";

    /**
     * Error message used when trying to refund a greater amount than the total merchant's funds.
     */
    public static final String ERROR_REASON_REFUND_TRANSACTION_AMOUNT_GREATER_THAN_MERCHANT_TOTAL_SUM =
            "Merchant's total transaction sum is less than the REFUND transaction's amount";

    /**
     * Constant for merchant ID property of {@link TransactionDto} class.
     */
    private static final String DTO_PROPERTY_MERCHANT_ID = "merchantId";

    /**
     * Merchant repository.
     */
    @Autowired
    private MerchantRepository merchantRepository;
    /**
     * Transaction repository.
     */
    @Autowired
    private TransactionRepository transactionRepository;
    /**
     * TransactionType repository.
     */
    @Autowired
    private TransactionTypeRepository transactionTypeRepository;
    /**
     * TransactionStatusType repository.
     */
    @Autowired
    private TransactionStatusTypeRepository transactionStatusTypeRepository;
    /**
     * The persistence context.
     */
    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Get a transaction by ID.
     *
     * @param id The ID of the transaction.
     * @return Optional value of a {@code TransactionDto}.
     */
    @Override
    @Transactional
    public Optional<TransactionDto> getById(@NotNull final UUID id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.map(TransactionDtoMapper::toDto);
    }

    /**
     * Delete transactions older than a specified date.
     *
     * @param dateBefore The date before which all transactions will be deleted.
     * @return Number of transactions deleted.
     */
    public int deleteOldTransactions(@NotNull final ZonedDateTime dateBefore) {
        return transactionRepository.deleteAllByCreatedDateBefore(dateBefore);
    }

    /**
     * Find transactions by a combination of search criteria.
     *
     * @param searchRequest      (optional) Contains the combination of search criteria.
     * @param pageSize           Maximum number of results in a page.
     * @param pageNum            The number of the page to return, starting from 0 (zero).
     * @param filterByMerchantId (optional) Enforce a mandatory filter by merchant ID. This will overwrite any
     *                           user-provided filters by merchant ID.
     * @return A paged result of merchants.
     */
    public Page<TransactionDto> findBySearchCriteria(@Nullable final SearchRequest searchRequest,
                                                     @NotNull final Integer pageSize, @NotNull final Integer pageNum,
                                                     @Nullable final Long filterByMerchantId) {

        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        SpecificationBuilder<Transaction> specificationBuilder =
                new SpecificationBuilder<>(TransactionSpecification::new, entityManager);

        if (searchRequest != null) {
            List<SearchCriteria> criteriaList = searchRequest.getSearchCriteriaList();
            if (filterByMerchantId != null) {
                if (criteriaList == null) {
                    criteriaList = new ArrayList<>(1);
                } else {
                    // Remove search criteria by merchantId if filterByMerchantId is provided
                    criteriaList =
                            criteriaList.stream().filter(it -> !it.getFilterKey().equals(DTO_PROPERTY_MERCHANT_ID))
                                    .collect(Collectors.toList());
                }

                SearchCriteria merchantSearchCriteria = new SearchCriteria();
                merchantSearchCriteria.setFilterKey(DTO_PROPERTY_MERCHANT_ID);
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
        Page<Transaction> searchResult = transactionRepository.findAll(specificationBuilder.build(), pageRequest);
        return searchResult.map(TransactionDtoMapper::toDto);
    }

    /**
     * Create a new transaction.
     *
     * @param transactionCreateRequest The transaction data.
     * @param merchantId               The merchant to which to associate the created transaction.
     * @return The created transaction.
     * @throws TransactionValidationException If the transaction cannot be created using the provided data.
     */
    @Transactional
    public TransactionDto add(@NotNull final TransactionCreateRequest transactionCreateRequest,
                              @NotNull final Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new TransactionValidationException("Merchant with ID [" + merchantId + "] does not exist"));

        Transaction transaction = new Transaction();
        transaction.setCustomerEmail(transactionCreateRequest.getCustomerEmail());
        transaction.setReferenceId(transactionCreateRequest.getReferenceId());
        transaction.setCustomerPhone(transactionCreateRequest.getCustomerPhone());
        transaction.setAmount(transactionCreateRequest.getAmount());
        transaction.setType(transactionTypeRepository.getReferenceById(transactionCreateRequest.getTypeId()));
        transaction.setMerchant(merchant);

        if (!MerchantStatusType.STATUS.ACTIVE.toString().equals(merchant.getStatus().getId())) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(ERROR_MESSAGE_MERCHANT_NOT_ACTIVE);
            return TransactionDtoMapper.toDto(transactionRepository.saveAndFlush(transaction));
        }
        if (TransactionType.TYPE.AUTHORIZE.toString().equals(transaction.getType().getId())) {
            processAuthTransaction(transactionCreateRequest, transaction);
        } else if (TransactionType.TYPE.CHARGE.toString().equals(transaction.getType().getId())) {
            processChargeTransaction(transactionCreateRequest, transaction);
        } else if (TransactionType.TYPE.REFUND.toString().equals(transaction.getType().getId())) {
            processRefundTransaction(transactionCreateRequest, transaction);
        } else if (TransactionType.TYPE.REVERSAL.toString().equals(transaction.getType().getId())) {
            processReversalTransaction(transactionCreateRequest, transaction);
        }
        return TransactionDtoMapper.toDto(transactionRepository.saveAndFlush(transaction));
    }

    private void processAuthTransaction(final TransactionCreateRequest transactionCreateRequest,
                                        final Transaction transaction) {

        transaction.setStatus(transactionStatusTypeRepository.getReferenceById(APPROVED.toString()));
    }

    private void processChargeTransaction(final TransactionCreateRequest transactionCreateRequest,
                                          final Transaction transaction) {

        Merchant merchant = merchantRepository.lockById(transaction.getMerchant().getId()).orElseThrow(
                () -> new TransactionValidationException(
                        "Merchant with ID [" + transaction.getMerchant().getId() + "] does not exist"));

        transaction.setStatus(transactionStatusTypeRepository.getReferenceById(APPROVED.toString()));

        merchant.setTotalTransactionSum(merchant.getTotalTransactionSum().add(transaction.getAmount()));
        merchantRepository.save(merchant);
    }


    private void processRefundTransaction(final TransactionCreateRequest transactionCreateRequest,
                                          final Transaction transaction) {
        UUID chargeTransactionId = Objects.requireNonNull(transactionCreateRequest.getBelongsToTransactionId(),
                "Property [belongsToTransactionId] is required for transaction type " + transaction.getType().getId());

        Transaction chargeTransaction = transactionRepository.lockById(chargeTransactionId).orElseThrow(
                () -> new TransactionValidationException(
                        "Transaction with ID [" + chargeTransactionId + "] does not exist."));


        if (!transaction.getMerchant().getId().equals(chargeTransaction.getMerchant().getId())) {
            throw new TransactionValidationException(ERROR_MESSAGE_TRANSACTION_BELONGS_ANOTHER_MERCHANT);
        }
        if (!chargeTransaction.getType().getId().equals(TransactionType.TYPE.CHARGE.toString())) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(ERROR_REASON_CANNOT_REFUND_TRANSACTION_OF_TYPE
                    + chargeTransaction.getType().getId());
            return;
        }

        if (!chargeTransaction.getStatus().getId().equals(APPROVED.toString())) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(
                    ERROR_REASON_CANNOT_REFUND_CHARGE_TRANSACTION_IN_STATUS + chargeTransaction.getStatus().getId());

            return;
        }

        if (chargeTransaction.getAmount().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(ERROR_REASON_REFUND_TRANSACTION_AMOUNT_GREATER_THAN_CHARGED);

            return;
        }

        Merchant merchant = merchantRepository.lockById(transaction.getMerchant().getId()).orElseThrow(
                () -> new TransactionValidationException(
                        "Merchant with ID [" + transaction.getMerchant().getId() + "] does not exist"));

        if (merchant.getTotalTransactionSum().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(ERROR_REASON_REFUND_TRANSACTION_AMOUNT_GREATER_THAN_MERCHANT_TOTAL_SUM);

            return;
        }

        chargeTransaction.setStatus(transactionStatusTypeRepository.getReferenceById(REFUNDED.toString()));
        transactionRepository.save(chargeTransaction);

        transaction.setStatus(transactionStatusTypeRepository.getReferenceById(APPROVED.toString()));
        transactionRepository.save(transaction);

        merchant.setTotalTransactionSum(merchant.getTotalTransactionSum().subtract(transaction.getAmount()));
        merchantRepository.save(merchant);

    }

    private void processReversalTransaction(final TransactionCreateRequest transactionCreateRequest,
                                            final Transaction transaction) {

        UUID chargeTransactionId = Objects.requireNonNull(transactionCreateRequest.getBelongsToTransactionId(),
                "Property [belongsToTransactionId] is required for transaction type " + transaction.getType().getId());

        Transaction authTransaction = transactionRepository.lockById(chargeTransactionId).orElseThrow(
                () -> new TransactionValidationException(
                        "Transaction with ID [ " + chargeTransactionId + "] does not exist."));

        if (!transaction.getMerchant().getId().equals(authTransaction.getMerchant().getId())) {
            throw new TransactionValidationException(ERROR_MESSAGE_TRANSACTION_BELONGS_ANOTHER_MERCHANT);
        }

        if (!authTransaction.getType().getId().equals(TransactionType.TYPE.AUTHORIZE.toString())) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(ERROR_REASON_CANNOT_REVERSE_TRANSACTION_OF_TYPE
                    + authTransaction.getType().getId());
            return;
        }

        if (!authTransaction.getStatus().getId().equals(APPROVED.toString())) {
            transaction.setStatus(transactionStatusTypeRepository.getReferenceById(ERROR.toString()));
            transaction.setErrorReason(
                    ERROR_REASON_CANNOT_REVERSE_AUTHORIZE_TRANSACTION_IN_STATUS + authTransaction.getStatus().getId());
            return;
        }

        transaction.setAmount(null); // Reversals do not have amount
        authTransaction.setStatus(transactionStatusTypeRepository.getReferenceById(REVERSED.toString()));
        transactionRepository.save(authTransaction);

        transaction.setStatus(transactionStatusTypeRepository.getReferenceById(APPROVED.toString()));
    }
}
