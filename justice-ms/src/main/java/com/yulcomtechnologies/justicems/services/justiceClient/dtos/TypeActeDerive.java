package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public
class TypeActeDerive {
    private Long id;
    private String libelle;
    private boolean statut;
    private Object parametreDTO;
}
