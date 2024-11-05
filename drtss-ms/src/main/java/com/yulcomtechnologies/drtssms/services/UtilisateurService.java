package com.yulcomtechnologies.drtssms.services;


import com.yulcomtechnologies.drtssms.dtos.UtilisateursDrtssDto;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.repositories.UtilisateursDrtssRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UtilisateurService {

private UtilisateursDrtssRepository utilisateursDrtssRepository;

    public UtilisateursDrtss createUtilisateur(UtilisateursDrtssDto utilisateursDrtssDto){

        UtilisateursDrtss save=  utilisateursDrtssRepository.save(UtilisateursDrtssDto.toEntity(utilisateursDrtssDto));
        return save;
    }


    public Page<UtilisateursDrtss> listUtilisateurs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return utilisateursDrtssRepository.findAll(pageable);
    }
}
