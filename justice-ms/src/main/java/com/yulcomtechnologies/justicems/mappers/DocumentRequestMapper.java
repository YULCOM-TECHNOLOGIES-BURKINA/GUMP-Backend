package com.yulcomtechnologies.justicems.mappers;

import com.yulcomtechnologies.justicems.dtos.DocumentRequestDto;
import com.yulcomtechnologies.justicems.dtos.FileDto;
import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import com.yulcomtechnologies.justicems.entities.File;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentRequestMapper {
    private final FileStorageService fileStorageService;


    public DocumentRequestDto toDto(
        DocumentRequest documentRequest
    ) {
        DocumentRequestDto dto = new DocumentRequestDto();
        BeanUtils.copyProperties(documentRequest, dto);
        //dto.setId(documentRequest.getId().toString());
        //dto.setRequesterId(documentRequest.getRequesterId());
        //dto.setStatus(documentRequest.getStatus());

        /*if (documentRequest.isApproved()) {
            var attestation = documentRequest.getAttestation();

            dto.setAttestation(
                new AttestationDto(
                        fileStorageService.getPath(attestation.getFile()),
                    attestation.getNumber(),
                    attestation.getExpirationDate().toLocalDate(), attestation.getFile().getPath()
                )
            );
        }*/

        /*if (documentRequest.getFiles() != null) {
            dto.setFiles(documentRequest.getFiles().stream()
                .map(this::fileToDto)
                .collect(Collectors.toSet()));
        }*/

        return dto;
    }

    public FileDto fileToDto(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setLabel(file.getLabel());
        fileDto.setPath(fileStorageService.getPath(file));
        return fileDto;
    }
}
