package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public
class Demandeur {
    private String nom;
    private String prenom;
    private String telephone;
    private String referencePiece = "";
    private String nomServiceAd = "";
    private String dossier = "";
    private String typeDemandeur;
}
