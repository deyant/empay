package com.example.empay.config;

import com.example.empay.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

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
