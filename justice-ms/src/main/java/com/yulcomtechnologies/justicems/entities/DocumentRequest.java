package com.yulcomtechnologies.justicems.entities;

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

    @Column(name = "requester_id", nullable = false, length = 255)
    private String requesterId;

    @Column(name = "rccm", nullable = false, length = 255)
    private String rccm;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "number", length = 255)
    private String number;

    @Column(name = "type", length = 255)
    private String type;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "status", length = 50, nullable = false)
    private String status = "PENDING";

    @Column(name = "rejection_date")
    private LocalDateTime rejectionDate;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_document_id", insertable = false, updatable = false)
    private File generatedDocument;

    @ManyToMany
    @JoinTable(
        name = "document_request_files",
        joinColumns = @JoinColumn(name = "document_request_id"),
        inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private Set<File> files = new HashSet<>();

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }
}
