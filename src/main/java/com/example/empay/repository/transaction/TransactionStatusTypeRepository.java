package com.example.empay.repository.transaction;

import com.example.empay.entity.transaction.TransactionStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionStatusTypeRepository extends JpaRepository<TransactionStatusType, String> {
}
