package com.yulcomtechnologies.drtssms.mappers;

import com.yulcomtechnologies.drtssms.dtos.AttestationDto;
import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.FileDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.services.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentRequestMapper {
    private final FileStorageService fileStorageService;

    public DocumentRequestDto toDto(DocumentRequest documentRequest) {
        DocumentRequestDto dto = new DocumentRequestDto();
        dto.setId(documentRequest.getId().toString());
        dto.setRequesterId(documentRequest.getRequesterId());
        dto.setStatus(documentRequest.getStatus());
        dto.setReviewedBy(documentRequest.getReviewedBy());
        dto.setApprovedBy(documentRequest.getApprovedBy());
        dto.setCreatedAt(documentRequest.getCreatedAt());

        if (documentRequest.isApproved()) {
            var attestation = documentRequest.getAttestation();

            dto.setAttestation(
                new AttestationDto(
                    fileStorageService.getPath(attestation.getFile()),
                    attestation.getNumber(),
                    attestation.getExpirationDate().toLocalDate()
                )
            );
            dto.setGeneratedDocumentUrl(fileStorageService.getPath(documentRequest.getAttestation().getFile()));
        }

        if (documentRequest.getFiles() != null) {
            dto.setFiles(documentRequest.getFiles().stream()
                .map(this::fileToDto)
                .collect(Collectors.toSet()));
        }

        return dto;
    }

    public FileDto fileToDto(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setLabel(file.getLabel());
        fileDto.setPath(fileStorageService.getPath(file));
        return fileDto;
    }
}
