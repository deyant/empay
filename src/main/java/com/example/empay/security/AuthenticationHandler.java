package com.example.empay.security;

import com.example.empay.dto.security.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Strategy used to handle a successful user authentication.
 */
@Component
@Slf4j
public class AuthenticationHandler implements AuthenticationSuccessHandler {

    /**
     * Object mapper instance.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     * @throws IOException
     */
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof EmpayUserDetails) {
            EmpayUserDetails userDetails = (EmpayUserDetails) authentication.getPrincipal();
            log.info("Successful authentication for UserLogin ID [{}]", userDetails.getUserLoginId());
            UserDto currentUser = new UserDto()
                    .setId(userDetails.getUserLoginId())
                    .setUsername(userDetails.getUsername())
                    .setMerchantId(userDetails.getMerchantId())
                    .setRoleId(userDetails.getAuthorities().size() > 0
                            ? userDetails.getAuthorities().toArray()[0].toString() : null);

            if (isAjaxRequest(request)) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().print(objectMapper.writeValueAsString(currentUser));
            } else {
                response.sendRedirect(request.getContextPath() + "/swagger-ui");
            }
        }
    }

    private boolean isAjaxRequest(final HttpServletRequest request) {
        return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getHeader(HttpHeaders.ACCEPT));
    }
}
