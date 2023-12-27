package com.example.empay.repository.security;

import com.example.empay.entity.security.RoleType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleTypeRepository extends CrudRepository<RoleType, String> {
}
