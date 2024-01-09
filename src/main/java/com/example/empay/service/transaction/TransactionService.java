package com.example.empay.service.transaction;

import com.example.empay.controller.search.SearchRequest;
import com.example.empay.dto.transaction.TransactionCreateRequest;
import com.example.empay.dto.transaction.TransactionDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all business logic for transactions.
 */
public interface TransactionService {

    /**
     * Get a transaction by ID.
     *
     * @param id The ID of the transaction.
     * @return Optional value of a {@code TransactionDto}.
     */
    Optional<TransactionDto> getById(@NotNull UUID id);

    /**
     * Delete transactions older than a specified date.
     *
     * @param dateBefore The date before which all transactions will be deleted.
     * @return Number of transactions deleted.
     */
    int deleteOldTransactions(@NotNull ZonedDateTime dateBefore);

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
    Page<TransactionDto> findBySearchCriteria(@Nullable SearchRequest searchRequest,
                                              @NotNull Integer pageSize, @NotNull Integer pageNum,
                                              @Nullable Long filterByMerchantId);

    /**
     * Create a new transaction.
     *
     * @param transactionCreateRequest The transaction data.
     * @param merchantId               The merchant to which to associate the created transaction.
     * @return The created transaction.
     */
    TransactionDto add(@NotNull TransactionCreateRequest transactionCreateRequest, @NotNull Long merchantId);
}
