package com.example.empay.security;

import com.example.empay.entity.security.UserLogin;
import com.example.empay.repository.security.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Provides the ability to create new users and update existing ones.
 */
@Slf4j
@Component
public class EmpayUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

    /**
     * UserLogin repository.
     */
    @Autowired
    private UserLoginRepository userLoginRepository;

    /**
     * Update the password of the specified user.
     *
     * @param user        the user to modify the password for
     * @param newPassword the password to change to, encoded by the configured
     *                    {@code PasswordEncoder}
     * @return
     */
    @Override
    public UserDetails updatePassword(final UserDetails user, final String newPassword) {
        log.debug("Update password for user: {}", user.getUsername());
        throw new IllegalStateException("Not yet implemented !");
    }

    /**
     * Create a new user using the data in the specified {@code UserDetails} instance.
     *
     * @param user The data used to create the new user.
     */
    @Override
    public void createUser(final UserDetails user) {
        throw new IllegalStateException("Not yet implemented !");
    }

    /**
     * Update an existing user using the data in the specified {@code UserDetails} instance.
     *
     * @param user The data used to update the existing user.
     */
    @Override
    public void updateUser(final UserDetails user) {
        throw new IllegalStateException("Not yet implemented !");
    }

    /**
     * Delete an existing user.
     *
     * @param username The username to identify the user to be deleted.
     */
    @Override
    public void deleteUser(final String username) {
        throw new IllegalStateException("Not yet implemented !");
    }

    /**
     * Change the password of the currently logged user.
     *
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    @Override
    public void changePassword(final String oldPassword, final String newPassword) {
        throw new IllegalStateException("Not yet implemented !");
    }

    /**
     * Check if a user with a specified username exists.
     *
     * @param username The username of the user to be checked for existance.
     * @return <code>true</code> if an user exists, <code>false</code> otherwise.
     */
    @Override
    public boolean userExists(final String username) {
        log.debug("Check if user exists: {}", username);
        Optional<UserLogin> userLogin = userLoginRepository.findByUsername(username);

        return userLogin.isPresent();
    }

    /**
     * Load a user with a specified username.
     *
     * @param username the username identifying the user whose data is required.
     * @return The loaded user.
     * @throws UsernameNotFoundException If no user exists with the specified username.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return EmpayUserDetails.fromUserLogin(userLoginRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User [%s] does not exist.", username))));

    }
}
