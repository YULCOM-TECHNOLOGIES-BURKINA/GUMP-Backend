package com.yulcomtechnologies.tresorms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "debiteurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebiteurEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String debiteur;
    private String promoteur;

    @Column(name = "numero_ifu")
    private String numeroIFU;

    @Column(name = "numero_immatriculation")
    private String numeroImmatriculation;

    @Column(name = "registre_commerce")
    private String registreCommerce;

    private String contacts;

    @Column(name = "date_naissance")
    private String dateNaissance;

    @Column(name = "numero_cnib")
    private String numeroCNIB;

    @Column(name = "numero_cheque")
    private String numeroCheque;

    @Column(name = "montant_du")
    private Double montantDu;
}
