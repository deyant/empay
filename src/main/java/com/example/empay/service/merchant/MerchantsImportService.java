package com.example.empay.service.merchant;

import com.example.empay.service.merchant.impl.MerchantImportException;

import java.io.IOException;
import java.io.Reader;


public interface MerchantsImportService {
    /**
     * Import merchants from a provided CSV content into the database.
     *
     * @param csvReader {@link Reader} of the CSV content that will be imported.
     * @return The number of merchants imported.
     * @throws MerchantImportException If an error during merchant import occurs.
     * @throws IOException             If an error while reading the CSV input source occurs.
     */
    Integer importMerchants(Reader csvReader) throws MerchantImportException, IOException;
}
