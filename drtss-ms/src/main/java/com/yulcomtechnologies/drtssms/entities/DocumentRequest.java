package com.yulcomtechnologies.drtssms.entities;

import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private String region;

    @Column(name = "contract_purpose", nullable = true)
    private String contractPurpose;

    @Column(name = "contracting_organization_name", nullable = true)
    private String contractingOrganizationName;

    @Column(name = "requester_id", nullable = false)
    private String requesterId;  // Keycloak user ID of the requester

    @Column(name = "public_contract_number")
    private String publicContractNumber;

    @Column(nullable = false)
    private String status = "PENDING";

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "signed_by")
    private String signedBy;



    @Column(name = "is_for_public_contract", nullable = false)
    private Boolean isForPublicContract;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "signed_at", nullable = true)
    private LocalDateTime signedAt;

    @ManyToOne
    @JoinColumn(name = "generated_document_id", referencedColumnName = "id")
    private File generatedDocument;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @ManyToMany
    @JoinTable(
        name = "document_request_files",
        joinColumns = @JoinColumn(name = "document_request_id"),
        inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<File> files = new HashSet<>();

    @OneToOne(mappedBy = "documentRequest", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Attestation attestation;

    public boolean isApproved() {
        return DocumentRequestStatus.APPROVED.name().equals(status);
    }
}
