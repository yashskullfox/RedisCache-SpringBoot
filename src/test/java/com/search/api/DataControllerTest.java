package com.search.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("build")
class DataControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void addAccount_success() throws Exception {
        String body = """
                {"type":"savings","value":"2500"}
                """;
        mockMvc.perform(post("/Account/200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("200")));
    }

    @Test
    void addAccount_missingValue_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/Account/201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"savings\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchAccount_credit() throws Exception {
        // first create
        mockMvc.perform(post("/Account/300")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"checking\",\"value\":\"1000\"}"));

        mockMvc.perform(patch("/Account/300")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"Credit\",\"value\":500}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("300")));
    }

    @Test
    void patchAccount_missingAction_returnsBadRequest() throws Exception {
        mockMvc.perform(patch("/Account/301")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAccount_success() throws Exception {
        // first create
        mockMvc.perform(post("/Account/400")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"savings\",\"value\":\"800\"}"));

        mockMvc.perform(delete("/Account/400")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("400")));
    }
}
