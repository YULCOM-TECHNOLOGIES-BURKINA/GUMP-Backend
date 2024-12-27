package com.yulcomtechnologies.justicems.controllers;

import com.yulcomtechnologies.feignClients.UserMsFeignClient;
import com.yulcomtechnologies.feignClients.dtos.CompanyDto;
import com.yulcomtechnologies.feignClients.dtos.UserDto;
import com.yulcomtechnologies.justicems.BaseIntegrationTest;
import com.yulcomtechnologies.justicems.dtos.CreatedResource;
import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import com.yulcomtechnologies.justicems.entities.File;
import com.yulcomtechnologies.justicems.enums.TypeDemandeEnum;
import com.yulcomtechnologies.justicems.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.justicems.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserData;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Disabled
class DocumentRequestControllerTest extends BaseIntegrationTest {
    @MockBean
    UserMsFeignClient userMsFeignClient;

    @MockBean
    AuthenticatedUserService authenticatedUserService;

    @Autowired
    DocumentRequestRepository documentRequestRepository;

    @Autowired
    FileRepository fileRepository;

    @MockBean
    FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        when(authenticatedUserService.getAuthenticatedUserData()).thenReturn(
            Optional.of(
                new AuthenticatedUserData("username", "role", "5")
            )
        );
    }

    @Test
    void submitsDocumentRequest() throws Exception {
        when(userMsFeignClient.getUser("5")).thenReturn(
            UserDto
                .builder()
                .company(new CompanyDto("Yulcom", "Ifu", "Ouaga", "123456789"))
                .build()
        );

        var response = mockMvc.perform(
            multipart("/demandes")
            .file("extraitRccm", "extraitRccm".getBytes())
            .file("statutEntreprise", "statutEntreprise".getBytes())
            .param("immatriculationDate", "2021-09-01")
            .param("typeDemande", TypeDemandeEnum.CERTIFICAT_NON_FAILLITE.name())
            .header("X-User-Id", "5")
        )
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn().getResponse().getContentAsString();

        var createdResourceId = Long.valueOf(objectMapper.readValue(response, CreatedResource.class).id());

        var document = documentRequestRepository.findById(createdResourceId).orElseThrow();

        assertEquals("5", document.getRequesterId());
        assertEquals("PENDING", document.getStatus());
        assertEquals(2, document.getFiles().size());
        assertEquals("123456789", document.getRccm());
        assertFalse(document.getIsPaid());
        assertEquals("Yulcom", document.getCompanyName());
        assertEquals(TypeDemandeEnum.CERTIFICAT_NON_FAILLITE.name(), document.getType());
    }

    @Test
    void getsDocumentRequests() throws Exception {
        var documentRequest = documentRequestRepository.save(
            DocumentRequest
                .builder()
                .requesterId("5")
                .number("number")
                .status("PENDING")
                .isPaid(false)
                .rccm("123456789")
                .createdAt(LocalDateTime.now())
                .companyName("Yulcom")
                .files(
                    Set.of(
                        fileRepository.save(
                            File
                                .builder()
                                .label("label")
                                .createdAt(LocalDateTime.now())
                                .path("path")
                                .build()
                        )
                    )
                )
                .type(TypeDemandeEnum.CERTIFICAT_NON_FAILLITE.name())
                .build()
        );

        mockMvc.perform(
            get("/demandes")
            .header("X-User-Id", "5")
        )
        .andDo(print())
        .andExpect(jsonPath("$.content[0].id").value(documentRequest.getId()))
        .andExpect(jsonPath("$.content[0].requesterId").value("5"))
        .andExpect(jsonPath("$.content[0].status").value("PENDING"))
        .andExpect(jsonPath("$.content[0].isPaid").value(false))
        .andExpect(jsonPath("$.content[0].companyName").value("Yulcom"))
        .andExpect(jsonPath("$.content[0].rccm").value("123456789"))
        .andExpect(jsonPath("$.content[0].type").value(TypeDemandeEnum.CERTIFICAT_NON_FAILLITE.name()))
        .andExpect(status().isOk());
    }
}
