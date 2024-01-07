package com.example.empay.service.merchant.impl;

import com.example.empay.dto.merchant.MerchantDto;
import com.example.empay.service.merchant.MerchantService;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class MerchantsImportServiceTests {

    @InjectMocks
    private MerchantsImportServiceImpl merchantsImportService;

    @Mock
    private MerchantService merchantService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Successful import of CSV file")
    @Test
    public void importCsvFileWithSuccess() throws IOException, MerchantImportException {
        Mockito.when(merchantService.add(Mockito.any(MerchantDto.class))).thenAnswer(mock -> mock.getArguments()[0]);
        Reader csvReader = new InputStreamReader(this.getClass().getResourceAsStream("/merchants-success.csv"));
        merchantsImportService.importMerchants(csvReader);
        Mockito.verify(merchantService, Mockito.times(2)).add(Mockito.any(MerchantDto.class));
    }

    @DisplayName("Failed import of CSV file due to failed constraint validation")
    @Test
    public void importCsvFileWithFailedValidation() throws IOException {
        HashSet violationSet = new HashSet<>();
        violationSet.add(TestUtil.createConstraintViolation("Test message", "invalid value", "email"));
        ConstraintViolationException ex = new ConstraintViolationException("Validation errors",
                new HashSet<ConstraintViolation<?>>(violationSet));

        Mockito.when(merchantService.add(Mockito.any(MerchantDto.class))).thenThrow(ex);
        Reader csvReader = new InputStreamReader(this.getClass().getResourceAsStream(
                "/merchants-failed-validation.csv"));

        try {
            merchantsImportService.importMerchants(csvReader);
        } catch (MerchantImportException e) {
            Assertions.assertNotNull(e.getMerchantDto());
            Assertions.assertEquals(0, e.getDataRecordIndex());
            Assertions.assertEquals("INVALID EMAIL !!!", e.getMerchantDto().getEmail());
        }
    }

    @DisplayName("Failed import of CSV file due to duplicate merchant identifier value")
    @Test
    public void importCsvFileFailedDuplicateMerchantIdentifierValue() throws IOException {
        Set violationSet = new HashSet<>();
        violationSet.add(TestUtil.createConstraintViolation("Test message", "invalid value", "identifierValue"));
        ConstraintViolationException ex = new ConstraintViolationException("Validation errors",
                new HashSet<ConstraintViolation<?>>(violationSet));

        Mockito.when(merchantService.add(Mockito.any(MerchantDto.class))).thenThrow(ex);
        Reader csvReader = new InputStreamReader(this.getClass().getResourceAsStream(
                "/merchants-failed-validation-duplicate-identifier.csv"));

        try {
            merchantsImportService.importMerchants(csvReader);
        } catch (MerchantImportException e) {
            Assertions.assertNotNull(e.getMerchantDto());
            Assertions.assertEquals(0, e.getDataRecordIndex());
            Assertions.assertEquals("100100100", e.getMerchantDto().getIdentifierValue());
        }

    }


}
