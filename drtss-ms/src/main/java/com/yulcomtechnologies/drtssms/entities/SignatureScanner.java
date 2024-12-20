package com.yulcomtechnologies.drtssms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yulcomtechnologies.sharedlibrary.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "signature_scanner")
public class SignatureScanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  //  @JsonIgnore
    //@OneToOne
    //@JoinColumn(name = "utilisateur_id",referencedColumnName = "id")
   // private UtilisateursDrtss utilisateur;*/

    @Column(name = "user_id", nullable = false,unique = true)
    private Long user_id;

    @Column(name = "email", nullable = false,unique = true)
    private String email;

    @Column(name = "region")
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "chemin_image")
    private String cheminImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


   @OneToOne(mappedBy = "signatureScanner")
   private SignatureCertificat signatureCertificat;

}
