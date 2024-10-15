package com.yulcomtechnologies.drtssms.mappers;

import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.FileDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;

import java.util.stream.Collectors;

public class DocumentRequestMapper {

    public static DocumentRequestDto toDto(DocumentRequest documentRequest) {
        DocumentRequestDto dto = new DocumentRequestDto();
        dto.setId(documentRequest.getId().toString());
        dto.setRequesterId(documentRequest.getRequesterId());
        dto.setStatus(documentRequest.getStatus());
        dto.setReviewedBy(documentRequest.getReviewedBy());
        dto.setApprovedBy(documentRequest.getApprovedBy());
        dto.setCreatedAt(documentRequest.getCreatedAt());

        if (documentRequest.getFiles() != null) {
            dto.setFiles(documentRequest.getFiles().stream()
                .map(DocumentRequestMapper::fileToDto)
                .collect(Collectors.toSet()));
        }

        return dto;
    }

    public static FileDto fileToDto(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setLabel(file.getLabel());
        fileDto.setPath(file.getPath());
        return fileDto;
    }
}
