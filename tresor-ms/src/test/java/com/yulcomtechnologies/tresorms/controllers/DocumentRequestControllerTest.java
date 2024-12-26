package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.tresorms.BaseIntegrationTest;
import com.yulcomtechnologies.tresorms.dtos.PayRequest;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.entities.Payment;
import com.yulcomtechnologies.tresorms.enums.PaymentStatus;
import com.yulcomtechnologies.tresorms.enums.RequestType;
import com.yulcomtechnologies.tresorms.events.PaymentSucceeded;
import com.yulcomtechnologies.tresorms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.tresorms.repositories.PaymentRepository;
import com.yulcomtechnologies.tresorms.services.AttestationGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
class DocumentRequestControllerTest extends BaseIntegrationTest {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    DocumentRequestRepository documentRequestRepository;

    @Autowired
    EventPublisher eventPublisher;

    @MockBean
    NotificationFeignClient notificationFeignClient;

    @Autowired
    AttestationGenerator attestationGenerator;

    @Test
    void pay() throws Exception {
        var documentRequest = documentRequestRepository.save(
            DocumentRequest
                .builder()
                .requesterId("1")
                .isForPublicContract(false)
                .requestType(RequestType.LIQUIDATION)
                .createdAt(LocalDateTime.now())
                .isPaid(false)
                .status("PENDING")
                .build()
        );

        mockMvc.perform(post("/demandes/" + documentRequest.getId() + "/pay")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PayRequest("http://localhost:8080"))))
            .andExpect(status().isOk());

        var payment = paymentRepository.findAll().get(0);
        assertEquals(documentRequest.getId(), payment.getDocumentRequestId());
    }

    @Test
    void generatesDocument() {
        var documentRequests = documentRequestRepository.saveAll(
            List.of(
                DocumentRequest
                    .builder()
                    .requesterId("1")
                    .isForPublicContract(false)
                    .requestType(RequestType.SOUMISSION)
                    .createdAt(LocalDateTime.now())
                    .isPaid(false)
                    .status("PENDING")
                    .build(),
                DocumentRequest
                    .builder()
                    .requesterId("1")
                    .isForPublicContract(false)
                    .requestType(RequestType.LIQUIDATION)
                    .createdAt(LocalDateTime.now())
                    .isPaid(false)
                    .status("PENDING")
                    .build()
            )
        );

        attestationGenerator.generateDocument(documentRequests.get(0).getId());
        attestationGenerator.generateDocument(documentRequests.get(1).getId());

    }

    @Test
    void successfulPaymentCallback() throws Exception {
        var documentRequest = documentRequestRepository.saveAndFlush(
            DocumentRequest
                .builder()
                .requesterId("1")
                .isForPublicContract(false)
                .requestType(RequestType.LIQUIDATION)
                .createdAt(LocalDateTime.now())
                .isPaid(false)
                .status("PENDING")
                .build()
        );

        var payment = paymentRepository.saveAndFlush(
            Payment.builder()
                .id(UUID.randomUUID().toString())
                .documentRequestId(documentRequest.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .amount(1500.)
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PENDING.toString())
                .build()
        );

        mockMvc.perform(post("/demandes/" + documentRequest.getId() +"/update-payment-status")
            .contentType(MediaType.APPLICATION_JSON)
            .param("paymentId", payment.getId()))
            .andExpect(status().isOk());

        var updatedPayment = paymentRepository.findById(payment.getId()).get();
        assertEquals(PaymentStatus.SUCCEEDED.toString(), updatedPayment.getStatus());
    }

    @Test
    @Disabled
    void paymentSucceededEventTest() {
        var documentRequest = documentRequestRepository.saveAndFlush(
            DocumentRequest
                .builder()
                .requesterId("1")
                .isForPublicContract(false)
                .requestType(RequestType.LIQUIDATION)
                .createdAt(LocalDateTime.now())
                .isPaid(false)
                .status("PENDING")
                .build()
        );

        var payment = paymentRepository.saveAndFlush(
            Payment.builder()
                .id(UUID.randomUUID().toString())
                .documentRequestId(documentRequest.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .amount(1500.)
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PENDING.toString())
                .build()
        );

        eventPublisher.dispatch(
            new PaymentSucceeded(payment.getId(), documentRequest.getId())
        );
    }
}
