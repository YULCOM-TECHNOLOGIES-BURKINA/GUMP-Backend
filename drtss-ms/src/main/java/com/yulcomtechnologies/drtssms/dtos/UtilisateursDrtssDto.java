package com.yulcomtechnologies.drtssms.dtos;

import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import jakarta.persistence.Column;
import lombok.*;

import java.time.Instant;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateursDrtssDto {

    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String matricule;

    private String titre_honorifique;

    private String tel;

    private String region;

    private String role;

    private String userType;

    private String lastname;

    private String username;

    private String forename;


    private boolean actif;

    public static UtilisateursDrtssDto toDTO(UtilisateursDrtss utilisateursDrtss){
        return Optional.ofNullable(utilisateursDrtss)
                .map(s -> UtilisateursDrtssDto.builder()
                        .id(s.getId())
                        .nom(s.getNom())
                        .prenom(s.getPrenom())
                        .email(s.getEmail())
                        .matricule(s.getMatricule())
                        .tel(s.getTel())
                        .actif(s.isActif())
                        .titre_honorifique(s.getTitre_honorifique())
                        .region(s.getRegion())
                        .role(s.getRole())
                        .lastname(s.getLastname())
                        .forename(s.getForename())
                        .userType(s.getUserType())
                        .username(s.getUsername())
                        .build()
                )
                .orElse(null);
    }

    public static UtilisateursDrtss toEntity(UtilisateursDrtssDto utilisateursDrtssDto) {

        return Optional.ofNullable(utilisateursDrtssDto)
                .map(dto -> {
                    UtilisateursDrtss utilisateurs = new UtilisateursDrtss();
                    utilisateurs.setId(dto.getId());
                    utilisateurs.setNom(dto.getNom());
                    utilisateurs.setPrenom(dto.getPrenom());
                    utilisateurs.setEmail(dto.getEmail());
                    utilisateurs.setMatricule(dto.getMatricule());
                    utilisateurs.setTitre_honorifique(dto.getTitre_honorifique());
                    utilisateurs.setTel(dto.getTel());
                    utilisateurs.setRegion(dto.getRegion());
                    utilisateurs.setUsername(dto.getUsername());
                    utilisateurs.setLastname(dto.getLastname());
                    utilisateurs.setForename(dto.getForename());
                    utilisateurs.setUserType(dto.getUserType());
                    utilisateurs.setRole(dto.getRole());
                    utilisateurs.setActif(dto.isActif());

                    return utilisateurs;
                })
                .orElse(null);

    }
    }
