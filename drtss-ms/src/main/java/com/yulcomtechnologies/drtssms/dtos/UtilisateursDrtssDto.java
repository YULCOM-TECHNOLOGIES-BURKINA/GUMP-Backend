package com.yulcomtechnologies.drtssms.dtos;

import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
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
                    utilisateurs.setActif(dto.isActif());
                    return utilisateurs;
                })
                .orElse(null);

    }
    }
