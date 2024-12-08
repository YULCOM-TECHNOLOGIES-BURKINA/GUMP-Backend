package com.yulcomtechnologies.tresorms.mappers;

import com.yulcomtechnologies.tresorms.dtos.DebiteurDTO;
import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import org.springframework.stereotype.Component;

@Component
public class DebiteurMapper {
    public DebiteurEntity toEntity(DebiteurDTO dto) {
        return DebiteurEntity.builder()
            .debiteur(dto.getDebiteur())
            .promoteur(dto.getPromoteur())
            .numeroIFU(dto.getNumeroIFU())
            .numeroImmatriculation(dto.getNumeroImmatriculation())
            .registreCommerce(dto.getRegistreCommerce())
            .contacts(dto.getContacts())
            .dateNaissance(dto.getDateNaissance())
            .numeroCNIB(dto.getNumeroCNIB())
            .numeroCheque(dto.getNumeroCheque())
            .montantDu(dto.getMontantDu())
            .build();
    }
}
