package com.yulcomtechnologies.tresorms.dtos;

import com.yulcomtechnologies.tresorms.enums.RequestType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentRequestDto {
    @NotNull
    private RequestType requestType;
    private String requesterId;
    @NotNull
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
}