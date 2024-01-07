package com.example.empay.service.merchant.impl;

import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;
import com.example.empay.dto.merchant.MerchantStatusTypeDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Objects;

/**
 * Parses CSV input file and provides an {@link Iterator} of {@link MerchantDto} objects.
 */
public class MerchantCsvParser implements AutoCloseable {

    /**
     * Index of the CSV column containing merchant name.
     */
    private static final int CSV_COLUMN_NAME = 0;
    /**
     * Index of the CSV column containing merchant email.
     */
    private static final int CSV_COLUMN_EMAIL = 1;
    /**
     * Index of the CSV column containing merchant status.
     */
    private static final int CSV_COLUMN_STATUS = 2;
    /**
     * Index of the CSV column containing merchant identifier type.
     */
    private static final int CSV_COLUMN_IDENT_TYPE = 3;
    /**
     * Index of the CSV column containing merchant identifier value.
     */
    private static final int CSV_COLUMN_IDENT_VALUE = 4;

    /**
     * CSV parser.
     */
    private final CSVParser parser;

    /**
     * Default constructor.
     *
     * @param csvReader A reader of the input CSV file. The file in EXCEL CSV format.
     * @throws IOException If an error during reading the file occurs.
     */
    public MerchantCsvParser(final Reader csvReader) throws IOException {
        parser = new CSVParser(csvReader, CSVFormat.EXCEL);
    }

    /**
     * Parse the CSV file.
     *
     * @return An {@link Iterator} of {@link MerchantDto} objects.
     */
    public Iterator<MerchantDto> parse() {
        return new MerchantIterator(parser.iterator());
    }

    /**
     * Close the CSV parser.
     *
     * @throws IOException If an error occurs while closing the resources.
     */
    @Override
    public void close() throws IOException {
        parser.close();
    }

    static class MerchantIterator implements Iterator<MerchantDto> {

        /**
         * Iterator of CSV records.
         */
        private final Iterator<CSVRecord> iterator;

        MerchantIterator(final Iterator<CSVRecord> iterator) {
            this.iterator = Objects.requireNonNull(iterator, "Argument [iterator] must not be null");
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public MerchantDto next() {
            CSVRecord record = iterator.next();
            if (record == null) {
                return null;
            }
            MerchantDto merchantDto = new MerchantDto()
                    .setName(record.get(CSV_COLUMN_NAME))
                    .setEmail(record.get(CSV_COLUMN_EMAIL))
                    .setStatus(new MerchantStatusTypeDto().setId(record.get(CSV_COLUMN_STATUS)));

            String identifierTypeId = record.get(CSV_COLUMN_IDENT_TYPE);
            if (identifierTypeId != null && !identifierTypeId.isEmpty()) {
                merchantDto.setIdentifierType(new MerchantIdentifierTypeDto().setId(identifierTypeId));
                merchantDto.setIdentifierValue(record.get(CSV_COLUMN_IDENT_VALUE));
            }

            return merchantDto;
        }
    }
}
