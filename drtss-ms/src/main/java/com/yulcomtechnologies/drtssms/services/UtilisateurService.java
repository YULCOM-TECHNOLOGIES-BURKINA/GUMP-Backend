package com.yulcomtechnologies.drtssms.services;


import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.dtos.UtilisateursDrtssDto;
import com.yulcomtechnologies.drtssms.entities.SignatureCertificat;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.SignatureCertificatRepository;
import com.yulcomtechnologies.drtssms.repositories.SignatureScannerRepository;
import com.yulcomtechnologies.drtssms.repositories.UtilisateursDrtssRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class UtilisateurService {

private final UtilisateursDrtssRepository utilisateursDrtssRepository;
private final SignatureScannerRepository signatureScannerRepository;
private final SignatureCertificatRepository signatureCertificatRepository;
private final UsersFeignClient usersFeignClient;



    public UserDto feignUtilisateur(String idUser){
         UserDto user=  usersFeignClient.getUser(idUser);
        return user;
    }

    /**
     *
     * @param utilisateursDrtssDto
     * @return
     */
    public UtilisateursDrtss createUtilisateur(UtilisateursDrtssDto utilisateursDrtssDto){
        utilisateursDrtssDto.setActif(true);
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


    /**
     * Activer & desactiver Utilisateur
     * @param idUser
     * @return
     */
    public UtilisateursDrtss update_status(Long idUser)  {

        Optional<UtilisateursDrtss> optionalUser = utilisateursDrtssRepository.findById(idUser);

         if (!optionalUser.isPresent()) {
             throw new IllegalArgumentException("Utilisateur avec ID " + idUser + " non trouvé.");
        }

        UtilisateursDrtss user = optionalUser.get();
        user.setActif(!user.isActif());

        utilisateursDrtssRepository.save(user);
        Optional<SignatureScanner> usersignatureScanner = signatureScannerRepository.findSignatureScannerByUserId(user.getId());
        if (usersignatureScanner.isPresent()  ) {
            SignatureCertificat certificat= usersignatureScanner.get().getSignatureCertificat();
            certificat.setActif(user.isActif());
            signatureCertificatRepository.save(certificat);
         }

        return user;

    }
}
