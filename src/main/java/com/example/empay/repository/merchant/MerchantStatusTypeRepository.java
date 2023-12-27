package com.example.empay.repository.merchant;

import com.example.empay.entity.merchant.MerchantStatusType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantStatusTypeRepository extends CrudRepository<MerchantStatusType, String> {
}
