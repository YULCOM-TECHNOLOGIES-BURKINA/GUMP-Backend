package com.yulcomtechnologies.drtssms.dtos;

import com.yulcomtechnologies.drtssms.entities.SignatureCertificat;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignataireCertificatDto {
    public Long id;
    public String alias;
    public String password;
    public String commonName;
    public String organization;
    public String organizationalUnit;
    public String country;
    public String emailAddress;
    public SignatureScanner signataire;
    private String cheminCertificat;
    private String certificatFile;

    public static SignataireCertificatDto toSignataireCertificatDTO(SignatureCertificat signataireCertificat) {
        return Optional.ofNullable(signataireCertificat)
                .map(s -> SignataireCertificatDto.builder()
                        .id(s.getId())
                        .alias(s.getAlias())
                        .commonName(s.getCommonName())
                        .organization(s.getOrganization())
                        .organizationalUnit(s.getOrganizationalUnit())
                        .country(s.getCountry())
                        .cheminCertificat(s.getCheminCertificat())
                        .certificatFile(s.getCertificatFile())
                        .signataire(s.getSignatureScanner())
                        //  .signataire(s.getSignatureScan().getUtilisateur())
                        .build()
                )
                .orElse(null);
    }



    public static SignatureCertificat toSignataireCertificatEntity(SignataireCertificatDto signataireCertificatDTO) {
        return Optional.ofNullable(signataireCertificatDTO)
                .map(dto -> {
                    SignatureCertificat signataireCertificat = new SignatureCertificat();
                    signataireCertificat.setId(dto.getId());
                    signataireCertificat.setAlias(dto.getAlias());
                    signataireCertificat.setCommonName(dto.getCommonName());
                    signataireCertificat.setOrganization(dto.getOrganization());
                    signataireCertificat.setOrganizationalUnit(dto.getOrganizationalUnit());
                    signataireCertificat.setCountry(dto.getCountry());
                    signataireCertificat.setCreatedAt(LocalDateTime.now());
                    signataireCertificat.setActif(true);
                    signataireCertificat.setSignatureScanner(dto.getSignataire());
                    signataireCertificat.setCertificatFile(dto.getCertificatFile());
                    signataireCertificat.setCheminCertificat(dto.getCheminCertificat());

                    return signataireCertificat;
                })
                .orElse(null);
    }
}
