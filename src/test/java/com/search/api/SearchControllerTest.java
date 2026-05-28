package com.search.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("build")
class SearchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void search_unknownAccount_returnsOkWithEmptyResult() throws Exception {
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"99999\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void search_existingAccount_returnsData() throws Exception {
        // Seed data first
        mockMvc.perform(post("/Account/500")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":\"savings\",\"value\":\"3000\"}"));

        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"500\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account").value(500))
                .andExpect(jsonPath("$.type").value("savings"))
                .andExpect(jsonPath("$.value").value(3000));
    }

    @Test
    void search_missingBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}
