package com.example.empay.dto.merchant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MerchantIdentifierTypeDto {

    /**
     * Merchant identifier type ID.
     */
    private String id;

    /**
     * Merchant identifier type name.
     */
    private String name;
}
