package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.*;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.services.DocumentRequestService;
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

    @PostMapping("demandes")
    public ResponseEntity<CreatedResource> submitDocumentRequest(
        @RequestParam("attestationCnss") MultipartFile attestationCnss,
        @RequestParam("attestationAnpe") MultipartFile attestationAnpe,
        @RequestParam("publicContractNumber") String publicContractNumber
    ) throws IOException {

        DocumentRequest documentRequest = documentRequestService.submitDocumentRequest(
            attestationCnss,
            attestationAnpe,
            publicContractNumber
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
