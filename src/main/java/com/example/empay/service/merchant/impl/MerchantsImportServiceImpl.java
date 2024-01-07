package com.example.empay.service.merchant.impl;

import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.service.merchant.MerchantsImportService;
import com.example.empay.service.merchant.MerchantService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

@Service
@Validated
public class MerchantsImportServiceImpl implements MerchantsImportService {

    /**
     * Used for merchant persisting.
     */
    @Autowired
    private MerchantService merchantService;

    /**
     * Import merchants from a provided CSV content into the database.
     *
     * @param csvReader {@link Reader} of the CSV content that will be imported. The caller is responsible to close the
     *                               reader after invocation of this method.
     * @return The number of merchants imported.
     * @throws MerchantImportException If an error during merchant import occurs.
     * @throws IOException             If an error while reading the CSV input source occurs.
     */
    @Transactional(rollbackFor = {MerchantImportException.class, IOException.class})
    public Integer importMerchants(final Reader csvReader) throws MerchantImportException, IOException {
        int count = 0;
        try (MerchantCsvParser parser = new MerchantCsvParser(csvReader)) {
            Iterator<MerchantDto> merchantDtoIterator = parser.parse();
            while (merchantDtoIterator.hasNext()) {
                MerchantDto nextMerchantDto = merchantDtoIterator.next();
                try {
                    merchantService.add(nextMerchantDto);
                } catch (DataIntegrityViolationException | ConstraintViolationException e) {
                    throw new MerchantImportException(String.format("Error while importing CSV line [%d]", count + 1),
                            count, nextMerchantDto, e);
                }
                count++;
            }
        }
        return count;
    }
}
