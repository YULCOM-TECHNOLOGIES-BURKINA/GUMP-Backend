package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.*;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.services.DocumentRequestService;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class DocumentController {
    private final DocumentRequestService documentRequestService;

    @PostMapping("demandes/{id}/pay")
    public ResponseEntity<PaymentRequestResponse> payForRequest(
        @PathVariable Long id,
        @Validated @RequestBody PayRequest payRequest
        ) {

        return ResponseEntity.ok(documentRequestService.pay(id, payRequest));
    }

    @PostMapping("demandes/{id}/update-payment-status")
    public ResponseEntity<Void> updatePaymentStatus(
        @PathVariable Long id,
        @RequestParam String paymentId
    ) {
        documentRequestService.updatePaymentStatus(id, paymentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("demandes/{id}/payment-status")
    public ResponseEntity<DocumentRequestPaymentStatus> getPaymentStatus(
        @PathVariable Long id
    ) {
        var documentRequest = documentRequestService.getDocumentRequest(id.toString());

        return ResponseEntity.ok(new DocumentRequestPaymentStatus(documentRequest.getIsPaid()));
    }

    @PostMapping("demandes")
    public ResponseEntity<CreatedResource> submitDocumentRequest(
        @RequestParam("attestationCnss") MultipartFile attestationCnss,
        @RequestParam("attestationAnpe") MultipartFile attestationAnpe,
        @RequestParam("publicContractNumber") String publicContractNumber,
        @RequestParam(value = "contractPurpose", required = false) String contractPurpose,
        @RequestParam(value = "contractingOrganizationName", required = false) String contractingOrganizationName,
        @RequestParam(value = "isForPublicContract", required = false, defaultValue = "false") Boolean isForPublicContract
    ) throws IOException {

        DocumentRequest documentRequest = documentRequestService.submitDocumentRequest(
            attestationCnss,
            attestationAnpe,
            publicContractNumber,
            isForPublicContract,
            contractPurpose,
            contractingOrganizationName
        );

        return ResponseEntity.ok(new CreatedResource(documentRequest.getId().toString()));
    }

    @GetMapping("demandes")
    public ResponseEntity<Page<DocumentRequestDto>> getDocumentRequests(Pageable pageable) {
        var documentRequests = documentRequestService.getPaginatedDocumentRequests(pageable);
        return ResponseEntity.ok(documentRequests);
    }

    @GetMapping("demandes/{id}")
    public ResponseEntity<DocumentRequestDto> getDocumentRequest(@PathVariable String id) {
        var documentRequest = documentRequestService.getDocumentRequest(id);
        return ResponseEntity.ok(documentRequest);
    }

    @PostMapping("demandes/{id}/review")
    public ResponseEntity<?> reviewDocumentRequest(
        @PathVariable Long id,
        @RequestParam("status") DocumentRequestStatus status,
        @RequestParam(required = false) String rejectionReason
    ) {
        documentRequestService.reviewDocumentRequest(id, status, rejectionReason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("demandes/{id}/approve")
    public ResponseEntity<?> approveDocumentRequest(
        @PathVariable Long id,
        @RequestBody @Validated ApproveDocumentRequestDto approveDocumentRequestDto
        ) throws IOException {
        documentRequestService.approveDocumentRequest(id, approveDocumentRequestDto);
        return ResponseEntity.ok().build();
    }

}
