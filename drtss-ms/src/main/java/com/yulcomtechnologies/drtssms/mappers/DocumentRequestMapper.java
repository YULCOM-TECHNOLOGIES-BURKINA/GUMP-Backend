package com.yulcomtechnologies.drtssms.mappers;

import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.FileDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import com.yulcomtechnologies.drtssms.services.FileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentRequestMapper {
    private final FileService fileService;

    public DocumentRequestDto toDto(DocumentRequest documentRequest) {
        DocumentRequestDto dto = new DocumentRequestDto();
        dto.setId(documentRequest.getId().toString());
        dto.setRequesterId(documentRequest.getRequesterId());
        dto.setStatus(documentRequest.getStatus());
        dto.setReviewedBy(documentRequest.getReviewedBy());
        dto.setApprovedBy(documentRequest.getApprovedBy());
        dto.setCreatedAt(documentRequest.getCreatedAt());

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
        fileDto.setPath(fileService.getPath(file));
        return fileDto;
    }
}
