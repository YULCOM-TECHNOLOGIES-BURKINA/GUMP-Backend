package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.BaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
public class DocumentRequestControllerTest extends BaseIntegrationTest {

    @Test
    void createsDocumentRequest() throws Exception {
        mockMvc.perform(post("/demandes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"documentType\": \"ID_CARD\", \"documentId\": \"1234567890\" }"))
            .andExpect(status().isCreated());
    }
}
