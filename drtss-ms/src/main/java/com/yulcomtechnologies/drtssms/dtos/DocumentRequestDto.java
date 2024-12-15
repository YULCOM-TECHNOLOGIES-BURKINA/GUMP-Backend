package com.yulcomtechnologies.drtssms.dtos;

import jakarta.persistence.Column;
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
    private String publicContractNumber;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private Set<FileDto> files;
    private AttestationDto attestation;
    private Boolean isPaid;
    private Boolean isPastDue;
    private Integer remainingDaysBeforeDueDate;
    private CompanyDto company;
    private Boolean isForPublicContract;
    private String contractPurpose;
    private String contractingOrganizationName;
}
