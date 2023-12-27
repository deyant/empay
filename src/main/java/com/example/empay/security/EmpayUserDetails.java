package com.example.empay.security;

import com.example.empay.entity.security.UserLogin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Provides core user information.
 */
@Getter
public class EmpayUserDetails extends User {

    /**
     * UserLogin entity ID.
     */
    private Long userLoginId;
    /**
     * ID of the merchant this user is assigned to.
     */
    private Long merchantId;

    /**
     * Email of this user.
     */
    private String email;

    /**
     * Sole constructor.
     *
     * @param username              Username
     * @param password              Password
     * @param enabled               Enabled
     * @param accountNonExpired     Account is not expired
     * @param credentialsNonExpired Credentials not expired
     * @param accountNonLocked      Account is not locked
     * @param authorities           List of authorities
     * @param userLoginId           ID of the user
     * @param merchantId            ID of the merchant this user is assigned to.
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public EmpayUserDetails(final String username, final String password, final boolean enabled,
                            final boolean accountNonExpired, final boolean credentialsNonExpired,
                            final boolean accountNonLocked, final Collection<? extends GrantedAuthority> authorities,
                            final Long userLoginId, final Long merchantId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.userLoginId = userLoginId;
        this.merchantId = merchantId;
    }

    /**
     * Creates a new instance using a {@link UserLogin} instance as input.
     *
     * @param userLogin The input
     * @return A new {@link EmpayUserDetails} instance.
     */
    public static EmpayUserDetails fromUserLogin(final UserLogin userLogin) {
        String username = userLogin.getUsername();
        String password = userLogin.getCurrentPassword();
        boolean enabled = userLogin.getEnabled();
        boolean accountExpired = userLogin.getExpired();
        boolean accountLocked = userLogin.getLocked();
        boolean passwordExpired = userLogin.getRequirePasswordChange();
        Long merchantId = userLogin.getMerchant() != null ? userLogin.getMerchant().getId() : null;

        List<GrantedAuthority> authorities = new ArrayList<>(1);
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userLogin.getRole().getId()));

        return new EmpayUserDetails(username, password, enabled, !accountExpired, !passwordExpired, !accountLocked,
                authorities, userLogin.getId(), merchantId);
    }
}
