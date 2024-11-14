package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.BaseIntegrationTest;
import com.yulcomtechnologies.drtssms.dtos.CompanyDto;
import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentRequestControllerTest extends BaseIntegrationTest {
    @Autowired
    DocumentRequestRepository documentRequestRepository;

    @MockBean
    UsersFeignClient usersFeignClient;

    @Test
    void getDocumentRequests() throws Exception {
        documentRequestRepository.saveAll(
            List.of(
                DocumentRequest
                    .builder()
                    .requesterId("5")
                    .isPaid(false)
                    .createdAt(LocalDateTime.now())
                    .status(DocumentRequestStatus.PENDING.name())
                    .publicContractNumber("1234")
                    .build(),

                DocumentRequest
                    .builder()
                    .requesterId("7")
                    .isPaid(true)
                    .createdAt(LocalDateTime.now().minusDays(100))
                    .status(DocumentRequestStatus.PENDING.name())
                    .publicContractNumber("1234")
                    .build()
            )
        );

        when(usersFeignClient.getUser(any()))
            .thenReturn(UserDto.builder().company(
                new CompanyDto(
                    "Yulcom",
                    "YulcomIFU",
                    "YulcomAddress"
                )
            ).build());

        mockMvc.perform(get("/demandes"))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.content[0].requesterId").value("5"))
            .andExpect(jsonPath("$.content[0].isPastDue").value(false))
            .andExpect(jsonPath("$.content[0].isPaid").value(false))

            .andExpect(jsonPath("$.content[0].company.name").value("Yulcom"))
            .andExpect(jsonPath("$.content[0].company.ifu").value("YulcomIFU"))
            .andExpect(jsonPath("$.content[0].company.address").value("YulcomAddress"))

            .andExpect(jsonPath("$.content[1].requesterId").value("7"))
            .andExpect(jsonPath("$.content[1].isPastDue").value(true))
            .andExpect(jsonPath("$.content[1].isPaid").value(true));

    }
}
