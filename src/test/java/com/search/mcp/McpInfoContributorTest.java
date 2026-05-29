package com.search.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("build")
class McpInfoContributorTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void actuatorInfo_containsSearchAndBalanceTools() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mcp.tools[?(@.name == 'searchAccount')]").exists())
                .andExpect(jsonPath("$.mcp.tools[?(@.name == 'getAccountBalance')]").exists());
    }

    @Test
    void actuatorInfo_toolsHaveVersionField() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mcp.tools[0].version").value("1"));
    }
}
