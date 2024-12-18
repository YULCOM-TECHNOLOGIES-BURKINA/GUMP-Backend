package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.BaseIntegrationTest;
import com.yulcomtechnologies.drtssms.dtos.CompanyDto;
import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.Attestation;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VerificationControllerTest extends BaseIntegrationTest {
    @MockBean
    UsersFeignClient usersFeignClient;

    @Autowired
    AttestationRepository attestationRepository;

    @Autowired
    FileRepository fileRepository;

    @Test
    void getDocumentData() throws Exception {
        when(usersFeignClient.getUsernameOrKeycloakId("123456")).thenReturn(
            UserDto.builder().company(CompanyDto.builder().name("Yulcom").build()).build()
        );

        attestationRepository.save(
            Attestation.builder()
                .number("123456")
                .uuid("uuid")
                .attestationAnpeDate(LocalDate.now())
                .attestationAnpeNumber("Anpe number")
                .file(fileRepository.save(new File("", "")))
                .createdAt(LocalDateTime.of(2021, 1, 1, 0, 0))
                .expirationDate(LocalDateTime.of(2200, 1, 1, 0, 0))
                .documentRequest(
                    DocumentRequest
                        .builder()
                        .region("CENTRE")
                        .status(DocumentRequestStatus.APPROVED.name())
                        .isForPublicContract(false)
                        .isPaid(false)
                        .publicContractNumber("Public contract number")
                        .contractPurpose("Contract purpose")
                        .contractingOrganizationName("Contracting organization")
                        .requesterId("123456")
                        .createdAt(LocalDateTime.now()).build()
                )
                .build()
        );

        mockMvc.perform(get("/verify-document/123456"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.documentNumber").value("123456"))
            .andExpect(jsonPath("$.documentOwner").value("Yulcom"))
            .andExpect(jsonPath("$.documentGenerationDate").value("01/01/2021"))
            .andExpect(jsonPath("$.documentExpirationDate").value("01/01/2200"))
            .andExpect(jsonPath("$.isDocumentValid").value(true));
    }
}
