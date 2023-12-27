package com.example.empay.integration;

import com.example.empay.EmpayApplication;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmpayApplication.class})
@WebAppConfiguration
@Sql(value = "/test-data-transaction.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DirtiesContext

public class TransactionSearchTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @WithUserDetails("merchant2")
    public void get_transaction_by_id_success() throws Exception {
        this.mockMvc.perform(get("/api/v1/transaction/6f683d71-dbcc-41ed-b552-51130c00852c"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("6f683d71-dbcc-41ed-b552-51130c00852c"));
    }

    @Test
    @WithUserDetails("merchant5")
    public void get_transaction_by_not_found_for_user() throws Exception {
        this.mockMvc.perform(get("/api/v1/transaction/6f683d71-dbcc-41ed-b552-51130c00852c"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("merchant5")
    public void get_transaction_by_invalid_UUID() throws Exception {
        this.mockMvc.perform(get("/api/v1/transaction/123"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value(StringContains.containsString("Failed to convert value")));
    }

    @Test
    @WithUserDetails("admin")
    public void search_transactions_by_status_user_admin() throws Exception {
        String jsonContent = """
                {
                	"dataOption":"all",
                	"searchCriteriaList":[
                		{
                			"filterKey" : "status.id",
                			"operation" : "eq",
                            "value" : "ERROR"
                		}
                	]
                }        
                """;
        this.mockMvc.perform(post("/api/v1/transaction/search")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithUserDetails("merchant3")
    public void search_transactions_by_status_user_merchant() throws Exception {
        String jsonContent = """
                {
                	"dataOption":"all",
                	"searchCriteriaList":[
                		{
                			"filterKey" : "status.id",
                			"operation" : "eq",
                            "value" : "ERROR"
                		}
                	]
                }        
                """;
        this.mockMvc.perform(post("/api/v1/transaction/search")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithUserDetails("admin")
    public void search_transactions_by_merchant_id_admin_user() throws Exception {
        String jsonContent = """
                {
                	"dataOption":"all",
                	"searchCriteriaList":[
                		{
                			"filterKey" : "merchantId",
                			"operation" : "eq",
                            "value" : "2"
                		}
                	]
                }
                                
                """;
        this.mockMvc.perform(post("/api/v1/transaction/search")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalElements").value(1));

    }

    @Test
    @WithUserDetails("merchant5")
    public void search_transactions_by_merchant_id_user_merchant_other_merchant_id() throws Exception {
        String jsonContent = """
                {
                	"dataOption":"all",
                	"searchCriteriaList":[
                		{
                			"filterKey" : "merchantId",
                			"operation" : "eq",
                            "value" : "2"
                		}
                	]
                }
                                
                """;
        this.mockMvc.perform(post("/api/v1/transaction/search")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.totalElements").value(2));

    }


}
