package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.tresorms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.tresorms.dtos.GetDocumentRequestDto;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.tresorms.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentRequestService {
    private DocumentRequestRepository repository;
    private DocumentRequestMapper documentRequestMapper;
    private final AttestationGenerator attestationGenerator;

    public DocumentRequest submitDocumentRequest(DocumentRequestDto documentRequestDto, Boolean isForPublicContract) {
        var documentRequest = new DocumentRequest();
        BeanUtils.copyProperties(documentRequestDto, documentRequest);
        documentRequest.setIsForPublicContract(true);
        documentRequest.setStatus(DocumentRequestStatus.PENDING.toString());
        documentRequest.setRequesterId(UUID.randomUUID().toString());
        return repository.save(documentRequest);
    }

    public Page<GetDocumentRequestDto> getPaginatedDocumentRequests(Pageable pageable) {
        return repository.findAll(pageable).map(documentRequestMapper::toDto);
    }

    public GetDocumentRequestDto getDocumentRequest(String id) {
        return repository.findById(Long.parseLong(id))
            .map(documentRequestMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));
    }

    public void approveDocumentRequest(Long id) throws IOException {
        var documentRequest = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));

        documentRequest.setStatus(DocumentRequestStatus.APPROVED.toString());
        attestationGenerator.generateDocument(id);
        repository.save(documentRequest);
    }
}

