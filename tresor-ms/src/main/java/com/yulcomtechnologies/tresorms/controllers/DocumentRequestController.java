package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.dtos.*;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.services.DocumentRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@AllArgsConstructor
public class DocumentRequestController {
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

    @PostMapping("demandes/{id}/review")
    public ResponseEntity<Void> reviewDocumentRequest(
        @PathVariable Long id
    ) {
        documentRequestService.rejectDocumentRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("demandes/{id}/reject")
    public ResponseEntity<Void> rejectDocumentRequest(
        @PathVariable Long id
    ) {
        documentRequestService.rejectDocumentRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("demandes")
    public ResponseEntity<CreatedResource> submitDocumentRequest(
        @RequestBody @Validated DocumentRequestDto documentRequestDto,
        @RequestParam(value = "publicContractNumber", required = false) String publicContractNumber,
        @RequestParam(value = "isForPublicContract", required = false, defaultValue = "true") Boolean isForPublicContract
    ) {

        DocumentRequest documentRequest = documentRequestService.submitDocumentRequest(documentRequestDto, isForPublicContract);

        return ResponseEntity.ok(new CreatedResource(documentRequest.getId().toString()));
    }

    @GetMapping("demandes")
    public ResponseEntity<Page<GetDocumentRequestDto>> getDocumentRequests(
        Pageable pageable,
        @RequestParam(required = false) String publicContractNumber
    ) {
        var documentRequests = documentRequestService.getPaginatedDocumentRequests(pageable, publicContractNumber);
        return ResponseEntity.ok(documentRequests);
    }

    @GetMapping("demandes/{id}")
    public ResponseEntity<GetDocumentRequestDto> getDocumentRequest(@PathVariable String id) {
        var documentRequest = documentRequestService.getDocumentRequest(id);
        return ResponseEntity.ok(documentRequest);
    }


    @PostMapping("demandes/{id}/approve")
    public ResponseEntity<?> approveDocumentRequest(
        @PathVariable Long id
    ) throws IOException {
        documentRequestService.approveDocumentRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("demandes/{id}/rollback-rejection")
    public ResponseEntity<?> rollbackRejection(
        @PathVariable Long id
    ) {
        documentRequestService.rollbackRejection(id);
        return ResponseEntity.ok().build();
    }



}
