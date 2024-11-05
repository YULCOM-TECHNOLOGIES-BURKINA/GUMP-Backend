package com.yulcomtechnologies.drtssms.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yulcomtechnologies.drtssms.entities.SignatureCertificat;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureScannerDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private UtilisateursDrtss utilisateur;

    private String filePath;

    private LocalDateTime createdAt;


    private SignatureCertificat signatureCertificat;

    public static SignatureScannerDto toSignataireCertificatDTO(SignatureScanner signatureScanner) {
        return Optional.ofNullable(signatureScanner)
                .map(s -> SignatureScannerDto.builder()
                        .id(s.getId())
                        .filePath(s.getCheminImage())
                        .signatureCertificat(s.getSignatureCertificat())
                        .utilisateur(s.getUtilisateur())
                        .build()
                )
                .orElse(null);
    }


}
