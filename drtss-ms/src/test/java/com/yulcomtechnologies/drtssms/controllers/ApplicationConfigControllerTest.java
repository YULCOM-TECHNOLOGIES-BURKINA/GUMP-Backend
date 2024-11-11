package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.BaseIntegrationTest;
import com.yulcomtechnologies.drtssms.dtos.UpdateApplicationConfigRequest;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApplicationConfigControllerTest extends BaseIntegrationTest {
    @Autowired
    ApplicationConfigRepository applicationConfigRepository;

    @Test
    void updatesConfigSuccessfully() throws Exception {
        var initialLogoPath = applicationConfigRepository.get().getLogo().getPath();

        mockMvc.perform(multipart(HttpMethod.PUT, "/application-config")
                .file(
                    new MockMultipartFile(
                        "logo",
                        "testfile.pdf",
                        MediaType.IMAGE_PNG_VALUE,
                        "Test file content".getBytes()
                    )
                )
                .file(new MockMultipartFile("updateApplicationConfigRequest", "",
                    "application/json",
                    objectMapper.writeValueAsBytes(UpdateApplicationConfigRequest.builder()
                        .footer("footer")
                        .header("header")
                        .validityTimeInMonths(100)
                        .processingTimeInDays(101)
                        .build())))
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andDo(print());

       var updatedConfig = applicationConfigRepository.get();
       assertEquals("footer", updatedConfig.getFooter());
       assertEquals("header", updatedConfig.getHeader());
       assertEquals(100, updatedConfig.getValidityTimeInMonths());
       assertEquals(101, updatedConfig.getProcessingTimeInDays());

       assertNotEquals(initialLogoPath, updatedConfig.getLogo().getPath());
    }
}
