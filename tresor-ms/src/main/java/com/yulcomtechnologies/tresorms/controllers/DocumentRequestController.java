package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.dtos.CreatedResource;
import com.yulcomtechnologies.tresorms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.tresorms.dtos.GetDocumentRequestDto;
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
    public ResponseEntity<Page<GetDocumentRequestDto>> getDocumentRequests(Pageable pageable) {
        var documentRequests = documentRequestService.getPaginatedDocumentRequests(pageable);
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
}
