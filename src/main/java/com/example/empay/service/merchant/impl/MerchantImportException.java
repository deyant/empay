package com.example.empay.service.merchant.impl;

import com.example.empay.dto.merchant.MerchantDto;
import lombok.Getter;

/**
 * Exception indicating that an error has occurred during import of merchants from a file or another source.
 */
@Getter
public class MerchantImportException extends Exception {

    /**
     * Index of the record in the import source that caused the exception.
     */
    private final Integer dataRecordIndex;

    /**
     * The {@link MerchantDto} object that caused the error.
     */
    private final MerchantDto merchantDto;

    /**
     * Constructor.
     *
     * @param message Exception message.
     * @param dataRecordIndex Index of the record in the import source that caused the exception.
     * @param merchantDto The MerchantDto object that caused the error.
     * @param cause The cause of the exception.
     */
    public MerchantImportException(final String message, final Integer dataRecordIndex,
                                   final MerchantDto merchantDto, final Throwable cause) {
        super(message, cause);
        this.dataRecordIndex = dataRecordIndex;
        this.merchantDto = merchantDto;
    }
}
