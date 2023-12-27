package com.example.empay.config;

import com.example.empay.security.AuthenticationHandler;
import com.example.empay.security.DefaultLogoutHandler;
import com.example.empay.security.EmpayAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Security configuration of the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    /** Authentication handler. */
    @Autowired
    private AuthenticationHandler authenticationHandler;

    /** Logout handler for this application. */
    @Autowired
    private DefaultLogoutHandler logoutHandler;

    /**
     * Create a PasswordEncoder bean for this application used to encode plain-text passwords.
     * @return A <code>PasswordEncoder</code> instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Create and configure security filter chain.
     * @param http Http security configuration
     * @return Configured <code>SecurityFilterChain</code>
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                                auth.requestMatchers(antMatcher("/h2-console/**"),
                                                antMatcher("/error"),
                                                antMatcher("/api-docs/**"),
                                                antMatcher("/swagger-ui/**")).permitAll()
                                        .requestMatchers(antMatcher("/**")).authenticated()
                )
                .headers(headers -> headers.frameOptions(customizer -> customizer.disable())) // Needed for H2 console
                // Fixme: Implement CSRF
                .csrf(csrf -> csrf.ignoringRequestMatchers(antMatcher("/**")))
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .formLogin(c -> c.successHandler(authenticationHandler));

        http.logout((logout) -> logout.logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));

        http.exceptionHandling(c ->
                c.authenticationEntryPoint(new EmpayAuthenticationEntryPoint("/login")));

        return http.build();
    }
}
