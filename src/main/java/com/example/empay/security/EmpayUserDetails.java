package com.example.empay.security;

import com.example.empay.entity.security.UserLogin;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;


/**
 * Provides core user information.
 */
@Getter
public class EmpayUserDetails extends User {

    /**
     * UserLogin entity ID.
     */
    private final Long userLoginId;
    /**
     * ID of the merchant this user is assigned to.
     */
    private final Long merchantId;

    /**
     * Sole constructor.
     *
     * @param userLogin User login entity object
     */
    public EmpayUserDetails(final UserLogin userLogin) {
        super(userLogin.getUsername(),
                userLogin.getCurrentPassword(),
                userLogin.getEnabled(),
                !userLogin.getExpired(),
                !userLogin.getRequirePasswordChange(),
                !userLogin.getLocked(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userLogin.getRole().getId())));

        this.userLoginId = userLogin.getId();
        this.merchantId = userLogin.getMerchant() != null ? userLogin.getMerchant().getId() : null;
    }
}
