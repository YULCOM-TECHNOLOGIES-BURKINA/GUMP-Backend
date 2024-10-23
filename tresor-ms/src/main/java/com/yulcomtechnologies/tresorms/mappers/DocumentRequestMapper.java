package com.yulcomtechnologies.tresorms.mappers;

import com.yulcomtechnologies.tresorms.dtos.AttestationDto;
import com.yulcomtechnologies.tresorms.dtos.GetDocumentRequestDto;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.services.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class DocumentRequestMapper {
    private final FileStorageService fileStorageService;

    public GetDocumentRequestDto toDto(DocumentRequest documentRequest) {
        var dto = new GetDocumentRequestDto();
        dto.setId(documentRequest.getId().toString());
        BeanUtils.copyProperties(documentRequest, dto);

        if (documentRequest.isApproved()) {
            var attestation = documentRequest.getAttestation();

            dto.setAttestation(
                new AttestationDto(
                    fileStorageService.getPath(attestation.getFile()),
                    attestation.getNumber(),
                    attestation.getExpirationDate().toLocalDate()
                )
            );
        }

        return dto;
    }
}
