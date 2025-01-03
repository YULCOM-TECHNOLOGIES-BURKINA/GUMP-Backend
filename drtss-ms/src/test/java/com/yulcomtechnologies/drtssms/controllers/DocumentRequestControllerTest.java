package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.BaseIntegrationTest;
import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.CompanyDto;
import com.yulcomtechnologies.drtssms.dtos.PayRequest;
import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.Attestation;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class DocumentRequestControllerTest extends BaseIntegrationTest {
    @Autowired
    DocumentRequestRepository documentRequestRepository;

    @Autowired
    AttestationRepository attestationRepository;

    @MockBean
    UsersFeignClient usersFeignClient;

    @Test
    @Disabled
    void approvesSuccessfully() throws Exception {
        var document = documentRequestRepository.save(
            DocumentRequest
                .builder()
                .requesterId("5")
                .isPaid(true)
                .isForPublicContract(true)
                .region("CENTRE")
                .createdAt(LocalDateTime.now())
                .status(DocumentRequestStatus.PENDING.name())
                .publicContractNumber("1234")
                .build()
        );

        mockMvc.perform(
            post("/demandes/" + document.getId() +"/approve")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(
                    new ApproveDocumentRequestDto(
                        "123",
                        "123",
                        LocalDate.now(),
                        LocalDate.now(
                    )
                )))
                .header("X-User-Id", "5")
        )
        .andExpect(status().isOk());
    }

    @Test
    void getDocumentRequests() throws Exception {
        var documents = documentRequestRepository.saveAll(
            List.of(
                DocumentRequest
                    .builder()
                    .requesterId("5")
                    .isPaid(false)
                    .isForPublicContract(true)
                    .region("CENTRE")
                    .createdAt(LocalDateTime.now())
                    .status(DocumentRequestStatus.PENDING.name())
                    .publicContractNumber("1234")
                    .build(),

                DocumentRequest
                    .builder()
                    .requesterId("7")
                    .region("CENTRE")
                    .isForPublicContract(true)
                    .isPaid(true)
                    .createdAt(LocalDateTime.now().minusDays(100))
                    .status(DocumentRequestStatus.PENDING.name())
                    .publicContractNumber("1234")
                    .build()
            )
        );

        var attestation = attestationRepository.save(
            Attestation.builder()
                .attestationAnpeDate(LocalDate.now())
                .documentRequest(documents.get(0))
                .attestationAnpeNumber("1234")
                .build()
        );


        when(usersFeignClient.getUsernameOrKeycloakId(any()))
            .thenReturn(UserDto.builder().company(
                new CompanyDto(
                    "Yulcom",
                    "YulcomIFU",
                    "YulcomAddress",
                    "",
                    "",
                    ""
                )
            ).build());

        mockMvc.perform(get("/demandes")
                .header("X-User-Id", "5")
                .header( "X-User-Role", "USER")
            )
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.content[0].requesterId").value("5"))
            .andExpect(jsonPath("$.content[0].isPastDue").value(false))
            .andExpect(jsonPath("$.content[0].isPaid").value(false))

            .andExpect(jsonPath("$.content[0].company.name").value("Yulcom"))
            .andExpect(jsonPath("$.content[0].company.ifu").value("YulcomIFU"))
            .andExpect(jsonPath("$.content[0].company.address").value("YulcomAddress"));

    }

    @Test
    @Disabled
    void payForDocumentRequest() throws Exception {
        mockMvc.perform(
            post(
            "/demandes/1/pay"
            )
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(
                    new PayRequest(
                        "DRTSS",
                        "http://localhost"
                    )
                ))
        ).andExpect(status().isOk())
        .andDo(print());
    }
}
