package com.example.empay.config;

import com.example.empay.entity.security.UserLogin;
import com.example.empay.security.EmpayUserDetails;
import com.example.empay.util.Constants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Configuration for JPA.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider",
        modifyOnCreate = false)
public class JpaConfig {

    /**
     * Persistence context used to load the user entity.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create a bean that returns the currently logged-in user for auditing purposes.
     *
     * @return An AuditorAware instance.
     */
    @Bean
    public AuditorAware<UserLogin> auditorAware() {
        return () -> {
            if (SecurityContextHolder.getContext().getAuthentication()
                    != null) {
                Object principal =
                        SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                if (principal instanceof EmpayUserDetails) {
                    EmpayUserDetails userDetails = (EmpayUserDetails) principal;
                    return Optional.of(
                            entityManager.getReference(UserLogin.class,
                                    userDetails.getUserLoginId()));
                }
            }
            return Optional.empty();
        };
    }

    /**
     * Provides <code>ZonedDateTime</code> in UTC timezone for JPA entity
     * auditing purposes.
     *
     * @return A <code>DateTimeProvider</code>
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(
                ZonedDateTime.now().withZoneSameInstant(Constants.ZONE_ID_UTC));
    }
}
