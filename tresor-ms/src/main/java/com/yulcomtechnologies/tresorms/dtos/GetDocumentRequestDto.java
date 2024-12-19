package com.yulcomtechnologies.tresorms.dtos;

import com.yulcomtechnologies.tresorms.enums.RequestType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetDocumentRequestDto {
    private String id;
    private RequestType requestType;
    private String requesterId;
    private String businessDomain;
    private String rccmReference;
    private String ifuNumber;
    private String address;
    private String phoneNumber;
    private String bankAccountReference;
    private String contractReference;
    private String contractPurpose;
    private String contractingOrganizationName;
    private String organizationAddress;
    private String organizationPhone;
    private String status;
    private int remainingDaysBeforeDueDate;
    private AttestationDto attestation;
    private LocalDateTime createdAt;
}
