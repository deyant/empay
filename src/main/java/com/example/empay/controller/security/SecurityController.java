package com.example.empay.controller.security;

import com.example.empay.dto.error.ErrorInfo;
import com.example.empay.dto.security.UserDto;
import com.example.empay.security.EmpayUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/security", produces = {"application/json"})
public class SecurityController {

    /**
     * Get details of the current logged user.
     *
     * @param userDetails Authentication details of the currently logged user.
     * @return Response with details of the currently logged user.
     */
    @GetMapping("/current")
    @Operation(summary = "Get a currently logged in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in."),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<UserDto> currentUser(@AuthenticationPrincipal final EmpayUserDetails userDetails) {

        if (userDetails != null) {
            UserDto currentUser = new UserDto()
                    .setId(userDetails.getUserLoginId())
                    .setUsername(userDetails.getUsername())
                    .setMerchantId(userDetails.getMerchantId());

            if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
                currentUser.setRoleId(userDetails.getAuthorities().toArray()[0].toString());
            }
            return new ResponseEntity<>(currentUser, HttpStatus.OK);
        }

        throw new AccessDeniedException("Not logged in.");
    }
}
