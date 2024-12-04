package com.yulcomtechnologies.justicems.controllers;

import com.yulcomtechnologies.justicems.dtos.DocumentRequestDto;
import com.yulcomtechnologies.justicems.services.DocumentRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class DocumentRequestController {
    private final DocumentRequestService documentRequestService;

    @PostMapping(path = "demandes")
    public ResponseEntity<Void> createDocumentRequest(
        @RequestParam(value = "attestationCnss", required = false) MultipartFile extraitRccm,
        @RequestParam(value = "attestationAnpe", required = false) MultipartFile statutEntreprise,
        @RequestParam LocalDate immatriculationDate
    ) {
        return ResponseEntity.ok().build();
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
}
