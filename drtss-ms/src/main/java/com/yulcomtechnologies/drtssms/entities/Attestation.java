package com.yulcomtechnologies.drtssms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attestations")
@AllArgsConstructor
@Data
@Builder
@ToString
public class Attestation {
    @Id
    @Column(name = "document_request_id")
    private Long id;

    private String uuid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "attestation_anpe_number")
    private String attestationAnpeNumber;

    @Column(name = "attestation_cnss_number")
    private String attestationCnssNumber;

    @Column(name = "attestation_anpe_date")
    private LocalDate attestationAnpeDate;

    @Column(name = "attestation_cnss_date")
    private LocalDate attestationCnssDate;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    @Column(unique = true)
    private String number;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    @OneToOne
    @MapsId
    @JoinColumn(name = "document_request_id")
    private DocumentRequest documentRequest;

    public Attestation() {
        uuid = UUID.randomUUID().toString();
    }
}
