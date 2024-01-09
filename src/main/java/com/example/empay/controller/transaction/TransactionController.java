package com.example.empay.controller.transaction;

import com.example.empay.controller.search.SearchRequest;
import com.example.empay.controller.search.SearchResponse;
import com.example.empay.dto.error.ErrorInfo;
import com.example.empay.dto.transaction.TransactionCreateRequest;
import com.example.empay.dto.transaction.TransactionDto;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.exception.SearchException;
import com.example.empay.exception.TransactionValidationException;
import com.example.empay.security.EmpayUserDetails;
import com.example.empay.service.transaction.TransactionService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "api/v1/transaction", produces = {"application/json"})
public class TransactionController {

    /**
     * Transaction service.
     */
    @Autowired
    private TransactionService service;

    /**
     * Load a transaction by ID.
     *
     * @param id          ID of the transaction to load.
     * @param userDetails Authentication details of the currently logged user.
     * @return Response with the transaction corresponding to the requested ID.
     */
    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get a transaction by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(schema = @Schema(implementation = TransactionDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request or failed validation.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<TransactionDto> getById(
            @PathVariable(name = "id")
            @Parameter(name = "id", description = "Transaction ID") final UUID id,
            @AuthenticationPrincipal final EmpayUserDetails userDetails) {

        TransactionDto transactionDto = service.getById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("%s with ID [%s] does not exist.",
                        Transaction.class.getSimpleName(), id)));

        Optional<GrantedAuthority> adminAuthority =
                userDetails.getAuthorities().stream().filter(it -> it.getAuthority().equals(Constants.ROLE_ADMIN))
                        .findFirst();

        if (adminAuthority.isEmpty() && !transactionDto.getMerchantId().equals(userDetails.getMerchantId())) {
            log.info("Access denied to transaction [{}] for user [{}]", id, userDetails.getUserLoginId());

            throw new EntityNotFoundException(String.format("%s with ID [%s] does not exist.",
                    Transaction.class.getSimpleName(), id));
        }
        return new ResponseEntity<>(transactionDto, HttpStatus.OK);
    }

    /**
     * Create a new transaction.
     *
     * @param transactionCreateRequest The new transaction data.
     * @param userDetails              Authentication details of the currently logged user.
     * @return Response with the created transaction.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_MERCHANT')")
    @Operation(summary = "Create a new transaction for the merchant of the currently logged in user with role "
            + "MERCHANT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful operation",
                    content = {@Content(schema = @Schema(implementation = TransactionDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request or failed validation.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "409", description = "Conflict. Unique constraint violated.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<TransactionDto> create(
            @Valid
            @RequestBody
            @Parameter(name = "transaction", description = "Transaction data.")
            final TransactionCreateRequest transactionCreateRequest,
            @AuthenticationPrincipal final EmpayUserDetails userDetails) {

        Long merchantId = Objects.requireNonNull(userDetails.getMerchantId(),
                "Current user is not assigned to a merchant");

        TransactionDto createdTransactionDto = service.add(transactionCreateRequest, merchantId);
        return new ResponseEntity<>(createdTransactionDto, HttpStatus.CREATED);
    }

    /**
     * Search for transactions using a combination of search criteria.
     *
     * @param pageNum       Return page identifier starting from 0 (zero).
     * @param pageSize      Size of the results page.
     * @param searchRequest Object containing the search criteria.
     * @param userDetails   Authentication object of the currently logged user.
     * @return A paged result.
     */
    @PreAuthorize("hasRole('ROLE_MERCHANT') or hasRole('ROLE_ADMIN')")
    @PostMapping("/search")
    @Operation(summary = "Search transactions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(schema = @Schema(implementation = SearchResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid request body provided.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<SearchResponse<TransactionDto>> search(
            @RequestParam(name = "pageNum", defaultValue = "0")
            @Parameter(name = "pageNum", description = "Request page number starting from zero.") final Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10")
            @Parameter(name = "pageSize", description = "Number of results per page.") final Integer pageSize,
            @RequestBody(required = false)
            @Parameter(name = "searchApiRequest", description = "Search request containing search criteria.")
            final SearchRequest searchRequest,
            @AuthenticationPrincipal final EmpayUserDetails userDetails) {


        Page<TransactionDto> page =
                service.findBySearchCriteria(searchRequest, pageSize, pageNum, userDetails.getMerchantId());

        SearchResponse<TransactionDto> apiResponse = new SearchResponse<>();
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
    @ExceptionHandler({TransactionValidationException.class})
    public ResponseEntity<ErrorInfo> handleTransactionValidation(final HttpServletRequest request,
                                                                 final TransactionValidationException e) {
        log.debug("Transaction validation error: {}", e.getMessage(), e);
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
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorInfo> handleEntityNotFoundException(final HttpServletRequest request,
                                                                   final EntityNotFoundException e) {
        log.debug("Entity not found exception: {}", e.getMessage(), e);
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
    @ExceptionHandler({SearchException.class})
    public ResponseEntity<ErrorInfo> handleSearchException(final HttpServletRequest request, final SearchException e) {
        log.debug("Error in search: {}", e.getMessage(), e);
        ErrorInfo errorInfo = new ErrorInfo(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
        return new ResponseEntity(errorInfo, HttpStatus.BAD_REQUEST);
    }
}
