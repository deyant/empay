package com.example.empay.repository.merchant;

import com.example.empay.entity.merchant.MerchantIdentifierType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantIdentifierTypeRepository extends CrudRepository<MerchantIdentifierType, String> {
}
