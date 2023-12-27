package com.example.empay.repository.transaction;

import com.example.empay.entity.transaction.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {


    /**
     * Select and lock a {@link Transaction} by ID using @{link LockModeType.PESSIMISTIC_WRITE} mode.
     *
     * @param id Transaction ID to be locked.
     * @return A {@link Transaction} instance.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.id = :id")
    Optional<Transaction> lockById(@Param("id") UUID id);

    /**
     * Bulk delete transactions having created date before a specified value.
     *
     * @param before The value before which all transactions will be deleted.
     * @return The number of deleted records.
     */
    int deleteAllByCreatedDateBefore(ZonedDateTime before);
}
