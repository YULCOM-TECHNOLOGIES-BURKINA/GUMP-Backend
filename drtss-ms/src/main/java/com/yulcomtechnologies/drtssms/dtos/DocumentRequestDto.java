package com.yulcomtechnologies.drtssms.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DocumentRequestDto {
    private String id;
    private String requesterId;
    private String status;
    private String reviewedBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private Set<FileDto> files;
}
