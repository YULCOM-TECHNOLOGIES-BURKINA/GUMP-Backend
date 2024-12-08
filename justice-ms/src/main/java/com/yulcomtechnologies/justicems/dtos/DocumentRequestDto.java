package com.yulcomtechnologies.justicems.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DocumentRequestDto {
    private String id;
    private String requesterId;
    private String status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private Set<FileDto> files;
    private FileDto generatedDocument;
    private Boolean isPaid;
    private String rccm;
    private String companyName;
    private String number;
    private String type;
}
