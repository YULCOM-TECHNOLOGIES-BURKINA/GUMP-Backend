package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Demande {
    private String documents;
    private String numeroRccm;
    private String numero;
    private String dateImatriculation;
    private String denominationSociete;
    private boolean affected;
    private boolean acteDownloaded;
    private boolean acteExpired;
    private String dateRejet;
    private String descriptionRejet;
    private String affectedTo;
    private String structure;
    private String dossier;
    private TypeActeDerive typeActeDerive;
    private Object typeDemande;
    private Object motifRejet;
    private Demandeur demandeur;
    private String statutDemande;
    private String created_date;
}
