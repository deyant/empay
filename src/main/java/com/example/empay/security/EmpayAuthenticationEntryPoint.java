package com.example.empay.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;
import java.util.Objects;

/**
 * Authentication entry point that supports both HTTP FORM and AJAX logins. Returns 403 FORBIDDEN if AJAX request is
 * attempted or redirects to the login URL otherwise.
 */
@Slf4j
public class EmpayAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * The URL of the default login form.
     */
    private String loginFormUrl;


    /**
     * Sole constructor.
     *
     * @param loginFormUrlArg The URL of the default login form to which the user will be redirected if trying to access
     *                        a resource that requires authentication.
     */
    public EmpayAuthenticationEntryPoint(final String loginFormUrlArg) {
        this.loginFormUrl = Objects.requireNonNull(loginFormUrlArg, "Argument [loginFormUrlArg] is required.");
    }

    /**
     * Commences an authentication scheme.
     *
     * @param request  that resulted in an <code>AuthenticationException</code>
     * @param response so that the user agent can begin authentication
     * @param ex       that caused the invocation
     * @throws IOException
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException ex)
            throws IOException {
        log.debug("Pre-authenticated entry point called. Rejecting access");
        if (isAjaxRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } else {
            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
            redirectStrategy.sendRedirect(request, response, loginFormUrl);
        }
    }

    private boolean isAjaxRequest(final HttpServletRequest request) {
        return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getHeader(HttpHeaders.ACCEPT));
    }
}
