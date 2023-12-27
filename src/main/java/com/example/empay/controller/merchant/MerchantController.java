package com.example.empay.controller.merchant;

import com.example.empay.controller.search.SearchRequest;
import com.example.empay.controller.search.SearchResponse;
import com.example.empay.dto.error.ConstraintValidationErrorInfo;
import com.example.empay.dto.error.ErrorInfo;
import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.security.EmpayUserDetails;
import com.example.empay.service.merchant.MerchantService;
import com.example.empay.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestController
@RequestMapping(path = "api/v1/merchant", produces = {"application/json"})
public class MerchantController {

    /**
     * Constant with the error message displayed when a merchant cannot be deleted due to existing transactions
     * related to that merchant.
     */
    public static final String ERROR_MERCHANT_CANNOT_BE_DELETED_HAS_TRANSACTIONS =
            "This merchant cannot be deleted because it has transactions.";
    /**
     * Constant with the error message displayed when a merchant is attempted to be created using a non-existing
     * IdentifierType.
     */
    public static final String ERROR_NON_EXISTING_IDENTIFIER_TYPE =
            "Non-existing identifier type specified in property [identifierTypeId].";

    /**
     * Constant with the error message displayed when a merchant is attempted to be created using a non-existing
     * MerchantStatusType.
     */
    public static final String ERROR_NON_EXISTING_STATUS =
            "Non-existing MerchantStatusType specified in property [statusId].";

    /**
     * Constant with the error message displayed when a merchant is attempted to be created using an already existing
     * combination of identifier type + value.
     */
    public static final String ERROR_IDENTITY_VALUE_CONFLICT =
            "Another merchant with the specified combination with identity type and value already exists.";

    /**
     * Constant with the error message displayed when a merchant is attempted to be created using an already existing
     * email address.
     */
    public static final String ERROR_EMAIL_CONFLICT = "Another merchant with the specified email already exists.";

    /**
     * Merchant service.
     */
    @Autowired
    private MerchantService merchantService;

    /**
     * Load a merchant by ID.
     *
     * @param id          ID of the merchant to load.
     * @param userDetails Authentication details of the currently logged user.
     * @return Response with the merchant corresponding to the requested ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get merchant by ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(schema = @Schema(implementation = MerchantDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request or failed validation.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "404", description = "Merchant not found.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<MerchantDto> getById(
            @PathVariable(name = "id") @Parameter(name = "id", description = "Merchant ID") final Long id,
            @AuthenticationPrincipal final EmpayUserDetails userDetails) {

        if (userDetails.getMerchantId() != null && !id.equals(userDetails.getMerchantId())) {
            throw new AccessDeniedException("Current user does not have access to other merchants than its own.");
        }

        MerchantDto merchantDto = merchantService.getById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("%s with ID [%s] does not exist.", Merchant.class.getSimpleName(), id)));

        return new ResponseEntity<>(merchantDto, HttpStatus.OK);
    }


    /**
     * Create a new merchant.
     *
     * @param merchant The new merchant data.
     * @return Response with the created merchant.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    @Operation(summary = "Create a new merchant.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Successful operation", content = {
            @Content(schema = @Schema(implementation = MerchantDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request or failed validation.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "409", description = "Conflict. Unique constraint violated.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<MerchantDto> create(
            @RequestBody
            @Parameter(name = "merchant", description = "Merchant data.")
            @Valid final MerchantDto merchant) {

        MerchantDto createdMerchantDto = merchantService.add(merchant);
        return new ResponseEntity<>(createdMerchantDto, HttpStatus.CREATED);
    }

    /**
     * Update an existing merchant.
     *
     * @param id       The ID of the merchant to update.
     * @param merchant The new merchant data.
     * @return Response with the updated merchant.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}")
    @Operation(summary = "Update a merchant.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(schema = @Schema(implementation = MerchantDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request or failed validation.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict. Unique constraint violated or optimistic lock failed.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<MerchantDto> update(
            @PathVariable(name = "id")
            @Parameter(name = "id", description = "Merchant ID") final Long id,
            @RequestBody
            @Parameter(name = "merchant", description = "Merchant data.")
            @Valid final MerchantDto merchant) {

        MerchantDto updatedMerchantDto = merchantService.update(id, merchant);
        return new ResponseEntity<>(updatedMerchantDto, HttpStatus.OK);
    }

    /**
     * Delete an existing merchant.
     *
     * @param id The ID of the merchant to delete.
     * @return Response with HTTP status NO_CONTENT if successful.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a merchant.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successful operation", content = {
            @Content(schema = @Schema(implementation = MerchantDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request or failed validation.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "409", description = "Conflict. Unique constraint violated.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity delete(
            @PathVariable(name = "id") @Parameter(name = "id", description = "Merchant ID") final Long id) {

        merchantService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Search for merchants using a combination of search criteria.
     *
     * @param pageNum       Return page identifier starting from 0 (zero).
     * @param pageSize      Size of the results page.
     * @param searchRequest Object containing the search criteria.
     * @param userDetails   Authentication object of the currently logged user.
     * @return A paged result.
     */
    @PostMapping("/search")
    @Operation(summary = "Search merchants.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation", content = {
            @Content(schema = @Schema(implementation = SearchResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request body provided.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<SearchResponse<MerchantDto>> search(
            @RequestParam(name = "pageNum", defaultValue = "0")
            @Parameter(name = "pageNum", description = "Request page number starting from zero.") final Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10")
            @Parameter(name = "pageSize", description = "Number of results per page.") final Integer pageSize,
            @RequestBody(required = false)
            @Parameter(name = "searchApiRequest", description = "Search request containing search criteria.")
            final SearchRequest searchRequest,
            @AuthenticationPrincipal final EmpayUserDetails userDetails) {

        Page<MerchantDto> page =
                merchantService.findBySearchCriteria(searchRequest, pageSize, pageNum, userDetails.getMerchantId());

        SearchResponse<MerchantDto> apiResponse = new SearchResponse<>();
        apiResponse.setData(page.toList());
        apiResponse.setTotalElements(page.getTotalElements());
        apiResponse.setTotalPages(page.getTotalPages());


        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * Exception handler method.
     *
     * @param request the request.
     * @param e       The thrown exception.
     * @return Response with error information.
     */
    @ExceptionHandler({PropertyReferenceException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorInfo> handleBadRequest(final HttpServletRequest request, final Exception e) {
        log.debug("Bad request: {}", e.getMessage(), e);
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST, e.getMessage(), request.getServletPath());
        return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handler method.
     *
     * @param request the request.
     * @param e       The thrown exception.
     * @return Response with error information.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleNotFound(final HttpServletRequest request, final Exception e) {
        log.debug("Entity not found: {}", e.getMessage(), e);
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
        return new ResponseEntity(errorInfo, HttpStatus.NOT_FOUND);
    }

    /**
     * Exception handler method.
     *
     * @param request the request.
     * @param e       The thrown exception.
     * @return Response with error information.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> handleDataIntegrityViolation(final HttpServletRequest request,
                                                                  final DataIntegrityViolationException e) {
        if (e.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
            if (Constants.FK_MERCHANT_STATUS_ID.equals(cve.getConstraintName())) {
                ErrorInfo errorInfo =
                        new ErrorInfo(HttpStatus.BAD_REQUEST, ERROR_NON_EXISTING_STATUS, request.getRequestURI());
                return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
            }

            if (Constants.FK_MERCHANT_IDENT_TYPE_ID.equals(cve.getConstraintName())) {
                ErrorInfo errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST, ERROR_NON_EXISTING_IDENTIFIER_TYPE,
                        request.getRequestURI());
                return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
            }

            if (cve.getConstraintName().contains(Constants.IDX_MERCHANT_EMAIL)) {
                ErrorInfo errorInfo = new ErrorInfo(HttpStatus.CONFLICT, ERROR_EMAIL_CONFLICT, request.getRequestURI());
                return new ResponseEntity(errorInfo, HttpStatus.CONFLICT);
            }

            if (cve.getConstraintName().contains(Constants.IDX_MERCHANT_IDENT)) {
                ErrorInfo errorInfo =
                        new ErrorInfo(HttpStatus.CONFLICT, ERROR_IDENTITY_VALUE_CONFLICT, request.getRequestURI());
                return new ResponseEntity(errorInfo, HttpStatus.CONFLICT);
            }

            if (cve.getConstraintName().contains(Constants.FK_TRANSACTION_MERCHANT_ID)) {
                ErrorInfo errorInfo =
                        new ErrorInfo(HttpStatus.CONFLICT, ERROR_MERCHANT_CANNOT_BE_DELETED_HAS_TRANSACTIONS,
                                request.getRequestURI());
                return new ResponseEntity(errorInfo, HttpStatus.CONFLICT);
            }

            log.error("Constraint violation: {}", e.getMessage(), e);
            ErrorInfo errorInfo =
                    new ErrorInfo(HttpStatus.BAD_REQUEST, "Database constraint violated: " + cve.getConstraintName(),
                            request.getRequestURI());
            return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
        }

        log.error("Data integrity violation: {}", e.getMessage(), e);
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
        return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception handler method.
     *
     * @param request the request.
     * @param e       The thrown exception.
     * @return Response with error information.
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ConstraintValidationErrorInfo> handleJakartaConstraintViolationException(
            final HttpServletRequest request, final jakarta.validation.ConstraintViolationException e) {

        ConstraintValidationErrorInfo errorInfo =
                new ConstraintValidationErrorInfo(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI(),
                        e.getConstraintViolations());

        return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
    }
}
