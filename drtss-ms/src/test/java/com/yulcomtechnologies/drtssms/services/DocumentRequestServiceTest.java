package com.yulcomtechnologies.drtssms.services;


import com.yulcomtechnologies.drtssms.BaseIntegrationTest;
import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.events.DocumentRequestChanged;
import com.yulcomtechnologies.drtssms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Disabled
public class DocumentRequestServiceTest extends BaseIntegrationTest {
    @Autowired
    DocumentRequestService documentRequestService;

    @Autowired
    DocumentRequestRepository documentRequestRepository;

    @Autowired
    AttestationRepository attestationRepository;

    @Autowired
    ApplicationConfigRepository applicationConfigRepository;

    @MockBean
    UsersFeignClient usersFeignClient;

    @MockBean
    NotificationFeignClient notificationFeignClient;

    @MockBean
    EventPublisher eventPublisher;

    DocumentRequest documentRequest;


    @Test
    @Transactional
    void approvesDocumentSuccessfully() throws IOException {
        documentRequest = DocumentRequest.builder()
            .isPaid(true)
            .createdAt(LocalDateTime.now())
            .requesterId("1")
            .publicContractNumber("1234")
            .status(DocumentRequestStatus.PROCESSING.name()).build();

        documentRequestRepository.save(documentRequest);

        when(usersFeignClient.getUser(documentRequest.getId().toString())).thenReturn(
            UserDto.builder()
                .id(1L)
                .email("test@gmail.com")
                .build()
        );


        var attestationCnssDate = LocalDate.now();
        var attestationAnpeDate = LocalDate.now().minusDays(2);

        var validateRequestDto = ApproveDocumentRequestDto.builder()
            .attestationAnpeDate(attestationAnpeDate)
            .attestationAnpeNumber("anpeNumber")
            .attestationCnssDate(attestationCnssDate)
            .attestationCnssNumber("cnssNumber")
            .build();

        documentRequestService.approveDocumentRequest(
            documentRequest.getId(),
            validateRequestDto
        );

        var updatedDocument = documentRequestRepository.findById(documentRequest.getId()).orElseThrow();
        assertEquals(DocumentRequestStatus.APPROVED.name(), updatedDocument.getStatus());
        var generatedAttestation = attestationRepository.findById(1L).orElseThrow();

        assertEquals("anpeNumber", generatedAttestation.getAttestationAnpeNumber());
        assertEquals("cnssNumber", generatedAttestation.getAttestationCnssNumber());
        assertEquals(attestationAnpeDate, generatedAttestation.getAttestationAnpeDate());
        assertEquals(attestationCnssDate, generatedAttestation.getAttestationCnssDate());

        assertEquals(LocalDate.now().plusMonths(applicationConfigRepository.get().getValidityTimeInMonths()).atTime(23, 59, 59), generatedAttestation.getExpirationDate());

        verify(eventPublisher).dispatch(
            new DocumentRequestChanged(updatedDocument.getId())
        );

    }
}
