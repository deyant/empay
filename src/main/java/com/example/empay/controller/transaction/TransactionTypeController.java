package com.example.empay.controller.transaction;

import com.example.empay.dto.error.ErrorInfo;
import com.example.empay.dto.transaction.TransactionTypeDto;
import com.example.empay.service.transaction.TransactionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping(path = "api/v1/transactionType", produces = {"application/json"})
public class TransactionTypeController {

    /**
     * TransactionType service.
     */
    @Autowired
    private TransactionTypeService service;

    /**
     * List all TransactionType values.
     *
     * @return A response with a list of TransactionType values.
     */
    @GetMapping
    @Operation(summary = "List all transaction types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = TransactionTypeDto.class)))),
            @ApiResponse(responseCode = "500", description =
                    "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<Collection<TransactionTypeDto>> listAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }
}
