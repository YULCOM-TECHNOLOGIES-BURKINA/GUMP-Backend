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


@Service
@AllArgsConstructor
public class UtilisateurService {

private UtilisateursDrtssRepository utilisateursDrtssRepository;

    /**
     *
     * @param utilisateursDrtssDto
     * @return
     */
    public UtilisateursDrtss createUtilisateur(UtilisateursDrtssDto utilisateursDrtssDto){

        UtilisateursDrtss save=  utilisateursDrtssRepository.save(UtilisateursDrtssDto.toEntity(utilisateursDrtssDto));
        return save;
    }


    /**
     *
     * @param page
     * @param size
     * @return
     */
    public Page<UtilisateursDrtss> listUtilisateurs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return utilisateursDrtssRepository.findAll(pageable);
    }
}
