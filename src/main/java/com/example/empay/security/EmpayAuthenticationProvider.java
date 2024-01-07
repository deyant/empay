package com.example.empay.security;

import com.example.empay.entity.security.UserLogin;
import com.example.empay.repository.security.UserLoginRepository;
import com.example.empay.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Authentication provider for this application responsible to load {@link UserDetails} instances.
 */
@Slf4j
@Component
public class EmpayAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * User login repository.
     */
    @Autowired
    private UserLoginRepository userLoginRepository;

    /**
     * The password encoder for this application.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Used in case of unsuccessful authentication to mitigate timing attacks.
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    /**
     * The password used to perform {@link PasswordEncoder#matches(CharSequence, String)}
     * on when the user is not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the password is not
     * in a valid format.
     */
    private volatile String userNotFoundEncodedPassword;


    /**
     * Sole constructor.
     *
     * @param passwordEncoder the {@link PasswordEncoder} to use.
     */
    public EmpayAuthenticationProvider(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieve user details using username and authentication token.
     *
     * @param username       The username to retrieve
     * @param authentication The authentication request, which subclasses <em>may</em>
     *                       need to perform a binding-based retrieval of the <code>UserDetails</code>
     * @return User details for the specified username.
     * @throws AuthenticationException If authentication cannot proceed.
     */
    protected UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        prepareTimingAttackProtection();
        Optional<UserLogin> userLogin = userLoginRepository.findByUsername(username);
        if (userLogin.isEmpty()) {
            mitigateAgainstTimingAttack(authentication);
            throw new UsernameNotFoundException(String.format("Failed login for user [%s]", username));
        }

        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userLogin.get().getCurrentPassword())) {
            log.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        EmpayUserDetails loadedUser = new EmpayUserDetails(userLogin.get());
        this.getPreAuthenticationChecks().check(loadedUser);
        additionalAuthenticationChecks(loadedUser, authentication);
        this.getPostAuthenticationChecks().check(loadedUser);
        return loadedUser;
    }

    /**
     * Called after successful authentication to perform additional authentication checks.
     *
     * @param userDetails    as retrieved from the
     *                       {@link #retrieveUser(String, UsernamePasswordAuthenticationToken)} or
     *                       <code>UserCache</code>
     * @param authentication the current request that needs to be authenticated
     * @throws AuthenticationException If authentication cannot proceed.
     */
    @Override
    protected void additionalAuthenticationChecks(final UserDetails userDetails,
                                                  final UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        EmpayUserDetails empayUserDetails = (EmpayUserDetails) userDetails;
        Optional<GrantedAuthority> merchantAuthority = empayUserDetails.getAuthorities().stream()
                .filter(it -> it.getAuthority().equals(Constants.ROLE_MERCHANT)).findFirst();

        if (merchantAuthority.isPresent() && empayUserDetails.getMerchantId() == null) {
            log.error("User [{}] has role MERCHANT but is not assigned to a merchant.",
                    empayUserDetails.getUserLoginId());
            throw new AuthenticationCredentialsNotFoundException("User not properly assigned to a merchant.");
        }
    }

    /**
     * Check if a type of authentication is supported by this provider.
     *
     * @param authentication The authentication class.
     * @return <code>true</code> if the authentication is supported, <code>false</code> otherwise.
     */
    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(final UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }
}
