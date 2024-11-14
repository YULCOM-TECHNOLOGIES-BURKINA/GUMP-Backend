package com.yulcomtechnologies.tresorms.entities;


import com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.tresorms.enums.RequestType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_requests")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DocumentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Column(name = "requester_id", nullable = false)
    private String requesterId;

    @Column(name = "business_domain")
    private String businessDomain;

    @Column(name = "rccm_reference")
    private String rccmReference;

    @Column(name = "ifu_number")
    private String ifuNumber;

    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "bank_account_reference")
    private String bankAccountReference;

    @Column(name = "contract_reference")
    private String contractReference;

    @Column(name = "contract_purpose")
    private String contractPurpose;

    @Column(name = "contracting_organization_name")
    private String contractingOrganizationName;

    @Column(name = "organization_address")
    private String organizationAddress;

    @Column(name = "organization_phone")
    private String organizationPhone;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "documentRequest", cascade = CascadeType.ALL)
    private Attestation attestation;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "rejectionReason")
    private String rejectionReason;

    @Column(name = "public_contract_number")
    private String public_contract_number;

    public boolean isApproved() {
        return status.equals(DocumentRequestStatus.APPROVED.toString());
    }
}
