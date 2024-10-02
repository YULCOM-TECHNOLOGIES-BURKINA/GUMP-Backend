package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.CreatedResource;
import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.services.DocumentRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class DocumentController {
    private final DocumentRequestService documentRequestService;

    @PostMapping("demandes")
    public ResponseEntity<CreatedResource> submitDocumentRequest(
        @RequestParam("requesterId") String requesterId,
        @RequestParam("document1") MultipartFile document1,
        @RequestParam("document2") MultipartFile document2) throws IOException {

        DocumentRequest documentRequest = documentRequestService.submitDocumentRequest(requesterId, document1, document2);

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
    public ResponseEntity<?> reviewDocumentRequest(@PathVariable Long id, @RequestParam("status") DocumentRequestStatus status) {
        documentRequestService.reviewDocumentRequest(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("demandes/{id}/approve")
    public ResponseEntity<?> approveDocumentRequest(
        @PathVariable Long id,
        @RequestBody ApproveDocumentRequestDto approveDocumentRequestDto
        ) {
        documentRequestService.approveDocumentRequest(id, approveDocumentRequestDto);
        return ResponseEntity.ok().build();
    }

}
