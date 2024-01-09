package com.example.empay.integration;

import com.example.empay.EmpayApplication;
import com.example.empay.entity.merchant.Merchant;
import com.example.empay.entity.transaction.Transaction;
import com.example.empay.entity.transaction.TransactionStatusType;
import com.example.empay.repository.merchant.MerchantRepository;
import com.example.empay.repository.transaction.TransactionRepository;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmpayApplication.class})
@WebAppConfiguration
@Sql(value = "/test-data-transaction.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext
public class TransactionCreateTests {
    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @WithUserDetails("merchant3")
    public void create_transaction_inactive_merchant_error() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "AUTHORIZE", 
                  "amount" : "55.55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.ERROR.toString()))
                .andExpect(jsonPath("$.errorReason").value("Merchant not active."));
    }

    @Test
    @WithUserDetails("merchant5")
    public void create_authorize_transaction_success() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "AUTHORIZE", 
                  "amount" : "55.55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "referenceId" : "ABC111111"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.APPROVED.toString()))
                .andExpect(jsonPath("$.amount").value(new BigDecimal("55.55")));

        Merchant merchant = merchantRepository.findById(1L).orElseThrow(() ->
                new IllegalStateException("Merchant not found"));

        // Assert that merchant's totalTransactionSum is not changed
        Assertions.assertEquals(BigDecimal.ZERO, merchant.getTotalTransactionSum().stripTrailingZeros());
    }


    @Test
    @WithUserDetails("merchant4")
    public void create_charge_transaction_success() throws Exception {
        Merchant merchant = merchantRepository.findById(4L).orElseThrow(() ->
                new IllegalStateException("Merchant not found"));

        BigDecimal totalTransactionSumBefore = merchant.getTotalTransactionSum();
        String jsonContent = """
                { 
                  "typeId" : "CHARGE", 
                  "amount" : "55.55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "referenceId" : "ABC222222"
                }
                """;

        BigDecimal transactionAmount = new BigDecimal("55.55");
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.APPROVED.toString()))
                .andExpect(jsonPath("$.amount").value(transactionAmount));

        merchant = merchantRepository.findById(4L).orElseThrow(() ->
                new IllegalStateException("Merchant not found"));

        BigDecimal transactionAmountRecalculated =
                merchant.getTotalTransactionSum().subtract(totalTransactionSumBefore);
        assertEquals(transactionAmount, transactionAmountRecalculated);
    }

    @Test
    @WithUserDetails("merchant2")
    public void create_refund_transaction_success() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REFUND", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "belongsToTransactionId" : "6f683d71-dbcc-41ed-b552-51130c00852c"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.APPROVED.toString()))
                .andExpect(jsonPath("$.amount").value(new BigDecimal("55")));

        Merchant merchant = merchantRepository.findById(2L).orElseThrow(() ->
                new IllegalStateException("Merchant not found"));

        assertEquals(merchant.getTotalTransactionSum(), new BigDecimal("45.23"));

        Transaction chargeTransaction = transactionRepository.findById(
                UUID.fromString("6f683d71-dbcc-41ed-b552-51130c00852c")).orElseThrow(() -> new IllegalStateException(
                "Charge transaction not found"));

        assertEquals(TransactionStatusType.TYPE.REFUNDED.toString(), chargeTransaction.getStatus().getId());
    }

    @Test
    @WithUserDetails("merchant2")
    public void create_refund_transaction_fail_greater_amount() throws Exception {
        Merchant merchant = merchantRepository.findById(2L).orElseThrow(() ->
                new IllegalStateException("Merchant not found"));

        BigDecimal totalTransactionSumBefore = merchant.getTotalTransactionSum();
        String jsonContent = """
                { 
                  "typeId" : "REFUND", 
                  "amount" : "55000", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "belongsToTransactionId" : "6f683d71-dbcc-41ed-b552-51130c00852c"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.ERROR.toString()))
                .andExpect(jsonPath("$.errorReason")
                        .value("Amount of the REFUND transaction is greater than the amount of the CHARGE " +
                                "transaction"));

        merchant = merchantRepository.findById(2L).orElseThrow(() ->
                new IllegalStateException("Merchant not found"));

        assertEquals(merchant.getTotalTransactionSum(), totalTransactionSumBefore);

        Transaction chargeTransaction = transactionRepository.findById(
                UUID.fromString("6f683d71-dbcc-41ed-b552-51130c00852c")).orElseThrow(() -> new IllegalStateException(
                "Charge transaction not found"));

        assertEquals(TransactionStatusType.TYPE.APPROVED.toString(), chargeTransaction.getStatus().getId());
    }

    @Test
    @WithUserDetails("merchant5")
    public void create_refund_transaction_fail_wrong_belongs_to_transaction_type() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REFUND", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "referenceId" : "ABC333333",
                  "belongsToTransactionId" : "3d7ae6ed-c794-47d4-ad11-7b0f53f09d6b"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.ERROR.toString()))
                .andExpect(jsonPath("$.errorReason").value(StringStartsWith.startsWith("Cannot refund")));
    }

    @Test
    @WithUserDetails("merchant5")
    public void create_refund_transaction_fail_invalid_belongs_to_id() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REFUND", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "referenceId" : "ABC333333",
                  "belongsToTransactionId" : "d2b2a551-fab6-4731-920e-566306cdd295"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("Transaction with ID [d2b2a551-fab6-4731-920e-566306cdd295] does not exist."));
    }

    @Test
    @WithUserDetails("merchant5")
    public void create_refund_transaction_fail_wrong_status() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REFUND", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "belongsToTransactionId" : "0f45e032-a74f-434f-b00e-e392ab340ab9"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.ERROR.toString()))
                .andExpect(jsonPath("$.errorReason").value(StringStartsWith.startsWith(
                        "Cannot refund a CHARGE transaction in status")));
    }

    @Test
    @WithUserDetails("merchant5")
    public void create_reversal_transaction_success() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REVERSAL", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "belongsToTransactionId" : "3d7ae6ed-c794-47d4-ad11-7b0f53f09d6b"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status.id").value(TransactionStatusType.TYPE.APPROVED.toString()));

        Transaction authTransaction = transactionRepository.findById(
                UUID.fromString("3d7ae6ed-c794-47d4-ad11-7b0f53f09d6b")).orElseThrow(() -> new IllegalStateException(
                "Charge transaction not found"));

        assertEquals(TransactionStatusType.TYPE.REVERSED.toString(), authTransaction.getStatus().getId());
    }

    @Test
    @WithUserDetails("merchant5")
    public void create_reversal_transaction_validation_failed() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REVERSAL", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456",
                  "belongsToTransactionId" : "3d7ae6ed-c794-47d4-ad11-7b0f53f09d6b"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithUserDetails("merchant5")
    public void create_refund_validation_failed_missing_belongs_to_transaction() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REFUND", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithUserDetails("merchant5")
    public void create_reversal_validation_failed_missing_belongs_to_transaction() throws Exception {
        String jsonContent = """
                { 
                  "typeId" : "REVERSAL", 
                  "amount" : "55", 
                  "customerEmail" : "john@nosuchemail.com",
                  "customerPhone" : "+123456"
                }
                """;
        this.mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

}
