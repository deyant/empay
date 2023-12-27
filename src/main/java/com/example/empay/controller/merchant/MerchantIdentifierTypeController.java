package com.example.empay.controller.merchant;

import com.example.empay.dto.error.ErrorInfo;
import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;
import com.example.empay.service.merchant.MerchantIdentifierTypeService;
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
@RequestMapping(path = "api/v1/merchantIdentifierType", produces = {"application/json"})
public class MerchantIdentifierTypeController {

    /**
     * MerchantIdentifierType service.
     */
    @Autowired
    private MerchantIdentifierTypeService service;

    /**
     * List all MerchantIdentifierType values.
     *
     * @return A response with a list of MerchantIdentifierType values.
     */
    @GetMapping
    @Operation(summary = "List all merchant identifier types.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MerchantIdentifierTypeDto.class)))),
            @ApiResponse(responseCode = "500", description =
                    "Internal Server Error. Contact support team for resolution.",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
    public ResponseEntity<Collection<MerchantIdentifierTypeDto>> listAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }
}
