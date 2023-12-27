package com.example.empay.dto.merchant;

import com.example.empay.dto.security.UserDto;
import com.example.empay.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class MerchantDto {

    /**
     * Merchant ID.
     */
    private Long id;

    /**
     * Merchant name.
     */
    @NotNull
    @NotBlank
    @Size(min = 1, max = Constants.LENGTH_NAME)
    private String name;

    /**
     * Merchant email.
     */
    @NotNull
    @NotBlank
    @Size(max = Constants.LENGTH_NAME)
    @Email
    private String email;

    /**
     * Merchant status.
     */
    @NotNull
    private MerchantStatusTypeDto status;

    /**
     * Identifier type.
     */
    private MerchantIdentifierTypeDto identifierType;

    /**
     * Merchant identifier value.
     */
    @Size(max = Constants.LENGTH_BUSINESS_IDENTIFIER)
    private String identifierValue;

    /**
     * Total transactions sum.
     */
    private BigDecimal totalTransactionSum;

    /**
     * Merchant date of creation.
     */
    private ZonedDateTime createdDate;
    /**
     * Merchant last modified datetime.
     */
    private ZonedDateTime lastModifiedDate;
    /**
     * Version number for optimistic locking.
     */
    private Integer version;
}
