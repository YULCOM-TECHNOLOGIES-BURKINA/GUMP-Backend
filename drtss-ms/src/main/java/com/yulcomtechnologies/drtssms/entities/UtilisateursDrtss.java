package com.yulcomtechnologies.drtssms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "utilisateurs_drtss")
public class UtilisateursDrtss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "matricule",unique = true)
    private String matricule;

    @Column(name = "titre_honorifique")
    private String titre_honorifique;

    @Column(name = "tel")
    private String tel;

    @Column(name = "region",nullable = false)
    private String region;

    @Column(name = "role",nullable = false)
    private String role;

    @Column(name = "user_type",nullable = false)
    private String userType;

    @Column(name = "lastname",nullable = true)
    private String lastname;

    @Column(name = "username",nullable = false)
    private String username;

    @Column(name = "forename",nullable = false)
    private String forename;


    @Column(name = "actif")
    private boolean actif = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
