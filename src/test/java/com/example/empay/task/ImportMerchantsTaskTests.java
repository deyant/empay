package com.example.empay.task;

import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.dto.merchant.MerchantIdentifierTypeDto;
import com.example.empay.dto.merchant.MerchantStatusTypeDto;
import com.example.empay.service.merchant.MerchantsImportService;
import com.example.empay.service.merchant.impl.MerchantImportException;
import com.example.empay.util.Constants;
import com.example.empay.util.TestUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
public class ImportMerchantsTaskTests {

    @Mock
    private MerchantsImportService merchantsImportService;

    @InjectMocks
    private ImportMerchantsTask importMerchantsTask;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Import CSV file success")
    @Test
    public void importCsvFileSuccess() throws IOException, MerchantImportException {
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenReturn(2);
        Reader csvReader = Mockito.mock(Reader.class);
        int merchantsCount = importMerchantsTask.importMerchants(csvReader);
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(2, merchantsCount);
    }

    @DisplayName("Failed import of CSV file due to ConstraintViolationException")
    @Test
    public void importCsvFileThrowsConstraintViolationException() throws IOException, MerchantImportException {
        Set violationSet = new HashSet<>();
        violationSet.add(TestUtil.createConstraintViolation("Test message", "invalid value", "email"));
        ConstraintViolationException cvEx = new ConstraintViolationException("Validation errors",
                new HashSet<ConstraintViolation<?>>(violationSet));

        MerchantImportException mEx = new MerchantImportException("error", 1, new MerchantDto(), cvEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to DataIntegrityViolationException for status ID")
    @Test
    public void importCsvFileThrowsDataIntegrityViolationForStatusId() throws IOException, MerchantImportException {
        org.hibernate.exception.ConstraintViolationException cve =
                new org.hibernate.exception.ConstraintViolationException("error message", null,
                        Constants.FK_MERCHANT_STATUS_ID);
        DataIntegrityViolationException dEx = new DataIntegrityViolationException("error message", cve);
        MerchantDto failedMerchant = new MerchantDto().setStatus(new MerchantStatusTypeDto().setId("WRONG ID"));
        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, dEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to DataIntegrityViolationException for identifier type ID")
    @Test
    public void importCsvFileThrowsDataIntegrityViolationForIdentifierTypeId() throws IOException,
            MerchantImportException {
        org.hibernate.exception.ConstraintViolationException cve =
                new org.hibernate.exception.ConstraintViolationException("error message", null,
                        Constants.FK_MERCHANT_IDENT_TYPE_ID);

        DataIntegrityViolationException dEx = new DataIntegrityViolationException("error message", cve);
        MerchantDto failedMerchant = new MerchantDto()
                .setIdentifierType(new MerchantIdentifierTypeDto().setId("WRONG ID"));

        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, dEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to duplicate for email")
    @Test
    public void importCsvFileFailedDuplicateEmail() throws IOException, MerchantImportException {
        org.hibernate.exception.ConstraintViolationException cve =
                new org.hibernate.exception.ConstraintViolationException("error message", null,
                        Constants.IDX_MERCHANT_EMAIL);

        DataIntegrityViolationException dEx = new DataIntegrityViolationException("error message", cve);
        MerchantDto failedMerchant = new MerchantDto().setEmail("invalid_email");
        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, dEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to duplicate for identifier value")
    @Test
    public void importCsvFileFailedDuplicateIdentifierValue() throws IOException, MerchantImportException {
        org.hibernate.exception.ConstraintViolationException cve =
                new org.hibernate.exception.ConstraintViolationException("error message", null,
                        Constants.IDX_MERCHANT_IDENT);

        DataIntegrityViolationException dEx = new DataIntegrityViolationException("error message", cve);
        MerchantDto failedMerchant = new MerchantDto()
                .setIdentifierType(new MerchantIdentifierTypeDto().setId("WRONG ID"))
                .setIdentifierValue("123");
        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, dEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to other constraint violation")
    @Test
    public void importCsvFileFailedOtherConstraintViolation() throws IOException, MerchantImportException {
        org.hibernate.exception.ConstraintViolationException cve =
                new org.hibernate.exception.ConstraintViolationException("error message", null,
                        "SOME_OTHER_CONSTRAINT");

        DataIntegrityViolationException dEx = new DataIntegrityViolationException("error message", cve);
        MerchantDto failedMerchant = new MerchantDto();
        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, dEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to other data integrity violation")
    @Test
    public void importCsvFileFailedOtherDataIntegrityViolation() throws IOException, MerchantImportException {

        DataIntegrityViolationException dEx = new DataIntegrityViolationException("error message",
                new SQLException("error"));

        MerchantDto failedMerchant = new MerchantDto();
        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, dEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to IOException")
    @Test
    public void importCsvFileFailedIOException() throws IOException, MerchantImportException {
        IOException ioEx = new IOException("error message");
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(ioEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Failed import of CSV file due to MerchantImportException")
    @Test
    public void importCsvFileFailedMerchantImportException() throws IOException, MerchantImportException {
        NullPointerException npEx = new NullPointerException("error message");
        MerchantDto failedMerchant = new MerchantDto();
        MerchantImportException mEx = new MerchantImportException("error", 1, failedMerchant, npEx);
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenThrow(mEx);
        int numberOfImportedMerchants = importMerchantsTask.importMerchants(Mockito.mock(Reader.class));
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
        Assertions.assertEquals(0, numberOfImportedMerchants);
    }

    @DisplayName("Successful import with correct arguments")
    @Test
    public void importCsvFileWithCorrectArguments() throws IOException, MerchantImportException {
        Mockito.when(merchantsImportService.importMerchants(Mockito.any(Reader.class))).thenReturn(2);
        importMerchantsTask.run("-task", "import:merchants", "src/test/resources/merchants-success.csv");
        Mockito.verify(merchantsImportService).importMerchants(Mockito.any(Reader.class));
    }

    @DisplayName("Failed import with incorrect arguments - missing import:merchants")
    @Test
    public void importCsvFileFailedIncorrectArgumentsMissingImportMerchants() throws IOException,
            MerchantImportException {
        importMerchantsTask.run("-task", "src/test/resources/merchants-success.csv");
        Mockito.verify(merchantsImportService, Mockito.times(0)).importMerchants(Mockito.any(Reader.class));
    }

    @DisplayName("Failed import with incorrect arguments - missing file path")
    @Test
    public void importCsvFileFailedIncorrectArgumentsMissingFilePath() throws IOException, MerchantImportException {
        importMerchantsTask.run("-task", "import:merchants");
        Mockito.verify(merchantsImportService, Mockito.times(0)).importMerchants(Mockito.any(Reader.class));
    }

    @DisplayName("Failed import with incorrect arguments - missing -task")
    @Test
    public void importCsvFileFailedIncorrectArgumentsMissingTask() throws IOException,
            MerchantImportException {
        importMerchantsTask.run("import:merchants", "src/test/resources/merchants-success.csv");
        Mockito.verify(merchantsImportService, Mockito.times(0)).importMerchants(Mockito.any(Reader.class));
    }

    @DisplayName("Failed import with incorrect arguments - wrong order")
    @Test
    public void importCsvFileFailedIncorrectArgumentsWrongOrder() throws IOException,
            MerchantImportException {
        importMerchantsTask.run("-task", "src/test/resources/merchants-success.csv", "import:merchants");
        Mockito.verify(merchantsImportService, Mockito.times(0)).importMerchants(Mockito.any(Reader.class));
    }

    @DisplayName("Failed import with incorrect arguments - file does not exist")
    @Test
    public void importCsvFileFailedIncorrectArgumentsFileNotExist() throws IOException,
            MerchantImportException {
        importMerchantsTask.run("-task", "import:merchants", "NON_EXISTING_FILE.csv");
        Mockito.verify(merchantsImportService, Mockito.times(0)).importMerchants(Mockito.any(Reader.class));
    }

}
