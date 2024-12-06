package com.yulcomtechnologies.justicems.controllers;

import com.yulcomtechnologies.justicems.BaseIntegrationTest;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
class DocumentRequestControllerTest extends BaseIntegrationTest {

    @Test
    void submitsDocumentRequest() throws Exception {
        mockMvc.perform(
            multipart("/demandes")
            .file("extraitRccm", "extraitRccm".getBytes())
            .file("statutEntreprise", "statutEntreprise".getBytes())
            .param("immatriculationDate", "2021-09-01")
            .header("X-User-Id", "")
        )
        .andExpect(status().isOk());
    }
}
