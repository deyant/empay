package com.example.empay.task;

import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.service.merchant.MerchantsImportService;
import com.example.empay.service.merchant.impl.MerchantImportException;
import com.example.empay.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Command-line runnable tasks that imports merchants from a CSV file into the database.
 */
@Component
@Slf4j
public class ImportMerchantsTask implements CommandLineRunner {

    /**
     * Constant for CLI argument task.
     */
    private static final String ARG_TASK = "-task";
    /**
     * Constant for CLI argument for importing merchants.
     */
    private static final String ARG_IMPORT_MERCHANTS = "import:merchants";
    /**
     * The offset of the argument import:merchants from the first argument.
     */
    private static final int ARG_IMPORT_MERCHANTS_OFFSET = 1;
    /**
     * The offset of the argument <csv_file_path> from the first argument.
     */
    private static final int ARG_CSV_FILE_OFFSET = 2;
    /**
     * Used for importing merchants.
     */
    @Autowired
    private MerchantsImportService merchantsImportService;

    /**
     * Callback used to run the bean.
     *
     * @param args Incoming CLI arguments. Example: -task import:merchants <path_to_csv_file>.
     */
    @Override
    public void run(final String... args) {
        boolean shouldExecute = false;
        String csvFilePath = null;
        for (int i = 0; i < args.length && !shouldExecute; i++) {
            if (ARG_TASK.equals(args[i])) {
                if (args.length >= i + ARG_IMPORT_MERCHANTS_OFFSET + 1
                        && ARG_IMPORT_MERCHANTS.equals(args[i + ARG_IMPORT_MERCHANTS_OFFSET])) {
                    shouldExecute = true;
                    if (args.length >= i + ARG_CSV_FILE_OFFSET + 1) {
                        csvFilePath = args[i + ARG_CSV_FILE_OFFSET];
                    }
                } else {
                    printHelp("Missing required argument: " + ARG_IMPORT_MERCHANTS);
                    return;
                }
            }
        }

        if (!shouldExecute) {
            return;
        }

        if (csvFilePath == null) {
            printHelp("Missing argument <path_to_csv_file>.");
            return;
        }

        try {
            log.info("Importing merchants from CSV file: {} ...", csvFilePath);
            Integer numberOfImportedMerchants = importMerchants(csvFilePath);
            log.info("Imported {} merchants from file {}.", numberOfImportedMerchants, csvFilePath);
        } catch (FileNotFoundException e) {
            printHelp(String.format("File %s does not exist.", csvFilePath));
        } catch (IOException e) {
            log.error("Error reading file {}: {}", csvFilePath, e.getMessage(), e);
        }

    }

    /**
     * Import merchants from a provided CSV file into the database.
     *
     * @param csvFilePath Path to a CSV file to be imported.
     * @return The number of merchants imported.
     */
    public Integer importMerchants(final String csvFilePath) throws IOException {
        File csvFile = new File(csvFilePath);
        try (Reader csvReader = new FileReader(csvFile)) {
            return importMerchants(csvReader);
        }
    }

    /**
     * Import merchants from a provided CSV file into the database.
     *
     * @param csvReader Reader of the CSV file that will be imported.
     * @return The number of merchants imported.
     */
    public Integer importMerchants(final Reader csvReader) {
        try {
            return merchantsImportService.importMerchants(csvReader);
        } catch (MerchantImportException e) {
            if (e.getCause() instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataEx = (DataIntegrityViolationException) e.getCause();
                log.error("Error while importing CSV line [{}}]: "
                                + getDataIntegrityViolationExceptionErrorMessage(dataEx, e.getMerchantDto()),
                        e.getDataRecordIndex() + 1);

                log.error("Transaction has been rolled-back, no database changes were made.");
            } else if (e.getCause() instanceof jakarta.validation.ConstraintViolationException) {
                jakarta.validation.ConstraintViolationException cvEx =
                        (jakarta.validation.ConstraintViolationException) e.getCause();

                String[] errorMessages = getConstraintViolationErrorMessages(cvEx);
                log.error("Error while importing CSV line [{}]: {} validation errors, see bellow.",
                        e.getDataRecordIndex() + 1,
                        errorMessages.length);

                for (int i = 0; i < errorMessages.length; i++) {
                    log.error("Error {}: {}", i + 1, errorMessages[i]);
                }
                log.error("Transaction has been rolled-back, no database changes were made.");
            } else {
                log.error(e.getMessage(), e);
                log.error("Transaction has been rolled-back, no database changes were made.");
            }
        } catch (IOException e) {
            log.error("Error while reading CSV file: " + e.getMessage());
        }
        return 0;
    }

    private String[] getConstraintViolationErrorMessages(final jakarta.validation.ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(it -> String.format("Invalid value [%s] for Merchant " + "property [%s]: %s", it.getInvalidValue(),
                        it.getPropertyPath(), it.getMessage())).collect(Collectors.toList()).toArray(new String[0]);

    }

    private String getDataIntegrityViolationExceptionErrorMessage(final DataIntegrityViolationException e,
                                                                  final MerchantDto merchantDto) {
        Objects.requireNonNull(e, "Argument [e] cannot be null.");
        Objects.requireNonNull(merchantDto, "Argument [merchantDto] cannot be null.");
        if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            org.hibernate.exception.ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
            if (Constants.FK_MERCHANT_STATUS_ID.equals(cve.getConstraintName())) {
                return "Invalid merchant status: "
                        + (merchantDto.getStatus() != null ? merchantDto.getStatus().getId() : null);
            }

            if (Constants.FK_MERCHANT_IDENT_TYPE_ID.equals(cve.getConstraintName())) {
                return String.format("Identifier type [%s] is not valid.",
                        merchantDto.getIdentifierType() != null ? merchantDto.getIdentifierType().getId() : null);
            }

            if (cve.getConstraintName() != null && cve.getConstraintName().contains(Constants.IDX_MERCHANT_EMAIL)) {
                return String.format("Another merchant with email [%s] already exists.", merchantDto.getId());
            }

            if (cve.getConstraintName().contains(Constants.IDX_MERCHANT_IDENT)) {
                return String.format("Another merchant with identifier type [%s] and value [%s] already exists.",
                        merchantDto.getIdentifierType() != null ? merchantDto.getIdentifierType().getId() : null,
                        merchantDto.getIdentifierValue());
            }
            return "Database constraint violated: " + cve.getConstraintName();
        } else {
            return e.getCause().getMessage();
        }
    }

    private void printHelp(final String errorMessage) {
        if (errorMessage != null) {
            log.error("Error: {}", errorMessage);
        }

        log.info("Usage: java empay.jar -task import:merchants <path_to_csv_file>");
    }
}
