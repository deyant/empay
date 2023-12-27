package com.example.empay.dto.transaction;

import com.example.empay.util.Constants;
import com.example.empay.validator.MustBeNullIfPropertyHasValue;
import com.example.empay.validator.RequiredIfPropertyHasValue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@RequiredIfPropertyHasValue(requiredProperty = "belongsToTransactionId",
        checkProperty = "typeId",
        checkPropertyValues = {"REFUND", "REVERSAL"},
        message = "Property [belongsToTransactionId] is required if transaction type is one of REFUND or REVERSAL")

@MustBeNullIfPropertyHasValue(nullProperty = "amount",
        checkProperty = "typeId",
        checkPropertyValues = {"REVERSAL"},
        message = "Property [amount] must be empty if transaction type is REVERSAL")
public class TransactionCreateRequest {

    /**
     * Transaction type ID.
     */
    @NotNull
    private String typeId;

    /**
     * Transaciton amount.
     */
    @Min(0)
    @Digits(integer = Constants.MAX_DECIMAL_INTEGER_DIGITS, fraction = Constants.MAX_DECIMAL_FRACTIONAL_DIGITS)
    private BigDecimal amount;

    /**
     * Customer email.
     */
    @Email
    @Size(max = Constants.LENGTH_EMAIL)
    private String customerEmail;

    /**
     * Customer phone.
     */
    @Size(max = Constants.LENGTH_PHONE)
    @Pattern(regexp = "^[\\+]{0,1}[\\d]{1,}$")
    private String customerPhone;

    /**
     * ID of a transaction this transaction belongs to.
     */
    private UUID belongsToTransactionId;

    /**
     * Business reference ID.
     */
    @Size(max = Constants.LENGTH_REFERENCE_ID)
    private String referenceId;
}
