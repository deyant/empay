package com.example.empay.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Default logout handler for this application.
 */
@Component
@Slf4j
public class DefaultLogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    /**
     * Called when logout is initiated. Causes the logout process to be completed.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the current principal details
     */
    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response,
                       final Authentication authentication) {
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof EmpayUserDetails) {
            EmpayUserDetails userDetails = (EmpayUserDetails) authentication.getPrincipal();
            log.info("Logout for UserLogin ID [{}]", userDetails.getUserLoginId());
        }
    }
}
