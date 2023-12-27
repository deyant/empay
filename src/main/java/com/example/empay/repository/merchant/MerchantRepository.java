package com.example.empay.repository.merchant;

import com.example.empay.entity.merchant.Merchant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long>, JpaSpecificationExecutor<Merchant> {

    /**
     * Select and lock a {@link Merchant} by ID using @{link LockModeType.PESSIMISTIC_WRITE} mode.
     *
     * @param id Merchant ID to be locked.
     * @return A {@link Merchant} instance.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Merchant m where m.id = :id")
    Optional<Merchant> lockById(@Param("id") Long id);
}
