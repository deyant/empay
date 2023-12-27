package com.example.empay.dto.merchant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MerchantStatusTypeDto {

    /**
     * Merchant status type ID.
     */
    private String id;

    /**
     * Merchant status type name.
     */
    private String name;
}
