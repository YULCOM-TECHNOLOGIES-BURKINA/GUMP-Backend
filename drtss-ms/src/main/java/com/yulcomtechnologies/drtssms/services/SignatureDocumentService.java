package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.SignataireCertificatDto;
import com.yulcomtechnologies.drtssms.entities.SignatureCertificat;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.enums.FileStoragePath;
import com.yulcomtechnologies.drtssms.repositories.SignatureCertificatRepository;
import com.yulcomtechnologies.drtssms.repositories.SignatureScannerRepository;
import com.yulcomtechnologies.drtssms.repositories.UtilisateursDrtssRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class SignatureDocumentService {

    private UtilisateursDrtssRepository utilisateursDrtssRepository;
    private SignatureCertificatRepository signatureCertificatRepository;
    private SignatureScannerRepository signatureScannerRepository;

    @Autowired
    private CertificateService certificateService;


    @Transactional
    public SignatureScanner createSignataire(MultipartFile file, Long userId) {
        String uploadPath = FileStoragePath.SCAN_SIGN_PATH.getPath();

        UtilisateursDrtss utilisateur = utilisateursDrtssRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + userId));
        try {
            Path directory = Paths.get(uploadPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            SignatureScanner signatureScan = new SignatureScanner();
            signatureScan.setCheminImage(fileName);
            signatureScan.setUtilisateur(utilisateur);
            //signatureScan.setDate_created(LocalDateTime.now());
            SignatureScanner signatureScanSave = signatureScannerRepository.save(signatureScan);

            SignataireCertificatDto certificatDTO = new SignataireCertificatDto();
            certificatDTO.setAlias(utilisateur.getPrenom() + '.' + utilisateur.getId());
            certificatDTO.setPassword("password");
            certificatDTO.setCommonName(utilisateur.getNom() + " " + utilisateur.getPrenom());
            certificatDTO.setOrganization("DRTPS - BY:" + utilisateur.getEmail());
            certificatDTO.setOrganizationalUnit("Certificat GUMP-DRTPS - :" + utilisateur.getNom() + utilisateur.getPrenom());
            certificatDTO.setCountry("BF");
            certificatDTO.setEmailAddress(utilisateur.getEmail());
            certificatDTO.setSignataire(signatureScanSave);

            certificateService.generateP12Certificate(certificatDTO);

            return signatureScanSave;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du fichier : " + e.getMessage(), e);
        } catch (DataAccessException e) {

            throw new RuntimeException("Erreur lors de l'enregistrement en base de données : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Une erreur inattendue s'est produite : " + e.getMessage(), e);
        }
    }

    public Page<SignatureScanner>listeSignataire(int page,int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return signatureScannerRepository.findAll(pageable);
    }
}
