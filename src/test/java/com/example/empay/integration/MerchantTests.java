package com.example.empay.integration;

import com.example.empay.EmpayApplication;
import com.example.empay.controller.merchant.MerchantController;
import com.example.empay.entity.merchant.MerchantStatusType;
import org.hamcrest.core.StringContains;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { EmpayApplication.class })
@WebAppConfiguration
@Sql(value = "/test-data-merchant.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext
public class MerchantTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @WithUserDetails("admin")
    public void get_merchant_by_id() throws Exception {
        this.mockMvc.perform(get("/api/v1/merchant/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithUserDetails("admin")
    public void get_merchant_by_id_invalid_long() throws Exception {
        this.mockMvc.perform(get("/api/v1/merchant/abc"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(StringContains.containsString("Failed to convert value")));
    }

    @Test
    @WithUserDetails("admin")
    public void get_merchant_by_id_not_found() throws Exception {
        this.mockMvc.perform(get("/api/v1/merchant/123"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(StringContains.containsString("does not exist")));
    }

    @Test
    @WithUserDetails("admin")
    public void create_merchant_success() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Another merchant", 
                  "email" : "another_merchant@nosuchemail.com", 
                  "status" : {
                    "id" : "ACTIVE"
                  }, 
                  "identifierType" : {
                    "id" : "EIK_BG"
                  }, 
                  "identifierValue" : "001122334455"
                }
                """;
        this.mockMvc.perform(post("/api/v1/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithUserDetails("admin")
    public void create_merchant_bad_request_too_long_name() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Too long name of this merchant over 100 characters. Too long name of this merchant over 100 characters. Too long name of this merchant over 100 characters. ", 
                  "email" : "merchant1123@test.com", 
                  "status" : {
                    "id" : "ACTIVE"
                  }
                }
                """;
        this.mockMvc.perform(post("/api/v1/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("admin")
    public void create_merchant_conflict_unique_email() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Another merchant", 
                  "email" : "merchant2@test.com", 
                  "status" : {
                    "id" : "ACTIVE"
                  }
                }
                """;
        this.mockMvc.perform(post("/api/v1/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(MerchantController.ERROR_EMAIL_CONFLICT));
    }

    @Test
    @WithUserDetails("admin")
    public void create_merchant_conflict_unique_identifierValue() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Another merchant", 
                  "email" : "another_merchant_new@nosuchemail.com", 
                  "status" : {
                    "id" : "ACTIVE"
                  }, 
                  "identifierType" : {
                    "id" : "EIK_BG"
                  }, 
                  "identifierValue" : "1111111111"
                }
                """;
        this.mockMvc.perform(post("/api/v1/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(MerchantController.ERROR_IDENTITY_VALUE_CONFLICT));
    }

    @Test
    @WithUserDetails("admin")
    public void create_merchant_bad_request_invalid_status() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Another merchant", 
                  "email" : "another_merchant_new@nosuchemail.com", 
                  "status" : {
                    "id" : "WRONG"
                  }
                }
                """;
        this.mockMvc.perform(post("/api/v1/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(MerchantController.ERROR_NON_EXISTING_STATUS));
    }

    @Test
    @WithUserDetails("admin")
    public void create_merchant_bad_request_invalid_identifierTypeId() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Another merchant", 
                  "email" : "another_merchant_new@nosuchemail.com", 
                  "status" : {
                    "id" : "ACTIVE"
                  }, 
                  "identifierType" : {
                    "id" : "WRONG"
                  }, 
                  "identifierValue" : "1234567890"
                }
                """;
        this.mockMvc.perform(post("/api/v1/merchant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(MerchantController.ERROR_NON_EXISTING_IDENTIFIER_TYPE));
    }

    @Test
    @WithUserDetails("admin")
    public void delete_merchant_fail_has_transactions() throws Exception {
        this.mockMvc.perform(delete("/api/v1/merchant/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(MerchantController.ERROR_MERCHANT_CANNOT_BE_DELETED_HAS_TRANSACTIONS));
    }

    @Test
    @WithUserDetails("admin")
    public void delete_merchant_success() throws Exception {
        this.mockMvc.perform(delete("/api/v1/merchant/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails("admin")
    public void update_merchant_success() throws Exception {
        String jsonContent = """
                { 
                  "name" : "Updated name", 
                  "email" : "merchant2@test.com", 
                  "status" : {
                    "id" : "INACTIVE"
                  }, 
                  "totalTransactionSum" : "999.99" 
                }
                """;
        this.mockMvc.perform(put("/api/v1/merchant/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.status.id").value(MerchantStatusType.STATUS.INACTIVE.toString()));
    }

    @Test
    @WithUserDetails("admin")
    public void search_merchants_success() throws Exception {
        String jsonContent = """
                {
                	"dataOption":"all",
                	"searchCriteriaList":[
                		{
                			"filterKey" : "email",
                			"operation" : "cn",
                            "value" : "@test.com"
                		}
                	]
                }
                                
                """;
        this.mockMvc.perform(post("/api/v1/merchant/search")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalElements").value(1));

    }
}
