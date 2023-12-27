package com.example.empay.dto.mapper;

import com.example.empay.dto.security.UserDto;
import com.example.empay.entity.security.UserLogin;

/**
 * A mapper between {@link UserDto} and {@link UserLogin} entity.
 */
public final class UserDtoMapper {

    /**
     * Hidden constructor.
     */
    private UserDtoMapper() {
    }

    /**
     * Create a new {@link UserDto} using a {@link UserLogin} instance.
     *
     * @param userLogin A UserLogin entity instance to copy the values from.
     * @return A new UserDto instance containing relevant values from the entity.
     */
    public static UserDto toDto(final UserLogin userLogin) {
        return new UserDto()
                .setId(userLogin.getId())
                .setUsername(userLogin.getUsername())
                .setRoleId(userLogin.getRole().getId())
                .setMerchantId(userLogin.getMerchant() != null ? userLogin.getMerchant().getId() : null);
    }
}
