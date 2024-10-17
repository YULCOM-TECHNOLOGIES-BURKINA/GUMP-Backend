package com.yulcomtechnologies.drtssms.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attestations")
public class Attestation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_request_id")
    private Long id;
    private String uuid;

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

    @Column(name = "document_path")
    private String documentPath;

    @OneToOne
    @MapsId
    @JoinColumn(name = "document_request_id")
    private DocumentRequest documentRequest;
}
