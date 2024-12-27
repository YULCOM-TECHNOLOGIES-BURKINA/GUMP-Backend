package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Demande {
    private String numeroRccm;
    private String dateImatriculation;
    private String denominationSociete;
    private TypeActeDerive typeActeDerive;
    private Demandeur demandeur;
    private String created_date;
    private String userConnected;
}
