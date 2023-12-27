package com.example.empay.repository.security;

import com.example.empay.entity.security.UserLogin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginRepository extends CrudRepository<UserLogin, Long> {

    /**
     * Find a {@link UserLogin} by username.
     *
     * @param username The username value to search on.
     * @return An optional value of {@link UserLogin} instance with the specified username.
     */
    Optional<UserLogin> findByUsername(String username);
}
