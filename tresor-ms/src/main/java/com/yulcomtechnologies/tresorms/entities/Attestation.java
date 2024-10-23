package com.yulcomtechnologies.tresorms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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
