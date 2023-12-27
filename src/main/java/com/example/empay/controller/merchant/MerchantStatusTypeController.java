package com.example.empay.controller.merchant;

import com.example.empay.dto.error.ErrorInfo;
import com.example.empay.dto.merchant.MerchantStatusTypeDto;
import com.example.empay.service.merchant.MerchantStatusTypeService;
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
@RequestMapping(path = "api/v1/merchantStatusType", produces = {"application/json"})
public class MerchantStatusTypeController {

    /**
     * MerchantStatusType service.
     */
    @Autowired
    private MerchantStatusTypeService service;

    /**
     * List all MerchantStatusType values.
     *
     * @return A response with a list of MerchantStatusType values.
     */
    @GetMapping
    @Operation(summary = "List all merchant status types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MerchantStatusTypeDto.class)))),
            @ApiResponse(responseCode = "500", description =
                    "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<Collection<MerchantStatusTypeDto>> listAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }
}
