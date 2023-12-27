package com.example.empay.dto.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class UserDto {

    /** User login ID. */
    private Long id;
    /** User name. */
    private String username;
    /** User role ID. */
    private String roleId;
    /** ID of the merchant this user belongs to. */
    private Long merchantId;
}
