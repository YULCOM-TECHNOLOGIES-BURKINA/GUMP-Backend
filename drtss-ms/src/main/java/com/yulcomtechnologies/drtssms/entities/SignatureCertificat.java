package com.yulcomtechnologies.drtssms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Table(name = "Signature_certificat")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureCertificat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alias",unique = true,nullable = false)
    private String alias;

    @Column(name = "nom_commun")
    private String commonName;

    @Column(name = "structure")
    private String organization;

    @Column(name = "organizational_unit")
    private String organizationalUnit;

    @Column(name = "pays")
    private String country;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "signature_scanner_id",referencedColumnName = "id")
    private SignatureScanner signatureScanner;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "actif")
    private boolean actif = true;

    @Column(name = "chemin_certificat")
    private String cheminCertificat;

    @Column(name = "certificat_file")
    private String certificatFile;

}
