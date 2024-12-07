package com.yulcomtechnologies.justicems.controllers;

import com.yulcomtechnologies.justicems.dtos.CreatedResource;
import com.yulcomtechnologies.justicems.dtos.DocumentRequestDto;
import com.yulcomtechnologies.justicems.enums.TypeDemandeEnum;
import com.yulcomtechnologies.justicems.services.DocumentRequestService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@AllArgsConstructor
public class DocumentRequestController {
    private final DocumentRequestService documentRequestService;

    @PostMapping(path = "demandes", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CreatedResource> createDocumentRequest(
        @RequestParam(value = "extraitRccm", required = false) MultipartFile extraitRccm,
        @RequestParam(value = "statutEntreprise", required = false) MultipartFile statutEntreprise,
        @RequestParam LocalDate immatriculationDate,
        @RequestParam TypeDemandeEnum typeDemande
    ) throws IOException {
        var documentRequest = documentRequestService.submitDocumentRequest(extraitRccm, statutEntreprise, immatriculationDate, typeDemande);

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
}
