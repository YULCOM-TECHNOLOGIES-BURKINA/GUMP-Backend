package com.yulcomtechnologies.justicems.services;

import com.yulcomtechnologies.feignClients.UserMsFeignClient;
import com.yulcomtechnologies.justicems.dtos.DocumentRequestDto;
import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import com.yulcomtechnologies.justicems.entities.File;
import com.yulcomtechnologies.justicems.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.justicems.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.justicems.repositories.FileRepository;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.Demande;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.Demandeur;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.TypeActeDerive;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentRequestService {
    private final DocumentRequestRepository documentRequestRepository;
    private final FileRepository fileRepository;
    private final DocumentRequestMapper documentRequestMapper;
    private final FileStorageService fileStorageService;
    private final AuthenticatedUserService authenticatedUserService;
    private final UserMsFeignClient userMsFeignClient;

    public DocumentRequest submitDocumentRequest(MultipartFile extraitRccm, MultipartFile statutEntreprise, LocalDate immatriculationDate) throws IOException {
        File extraitRccmDocument = saveFile(extraitRccm, "Recepissé RCCM");
        File statutEntrepriseDocument = saveFile(statutEntreprise, "Statut Entreprise");

        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));
        var currentUserInfo = userMsFeignClient.getUser(currentUser.getKeycloakUserId());

        var documentRequest = DocumentRequest.builder()
            .requesterId(currentUser.getKeycloakUserId())
            .createdAt(LocalDateTime.now())
            .status("PENDING").build();

        Set<File> files = new HashSet<>();

        if (extraitRccmDocument != null) {
            files.add(extraitRccmDocument);
        }

        if (statutEntrepriseDocument != null) {
            files.add(statutEntrepriseDocument);
        }
        documentRequest.setFiles(files);
        documentRequest.setCompanyName(currentUserInfo.getCompany().getName());
        documentRequest.setRccm(currentUserInfo.getCompany().getRccm());

        return documentRequestRepository.save(documentRequest);
    }

    private Demande createDemande(MultipartFile extraitRccm, MultipartFile statutEntreprise, LocalDate immatriculationDate) {
        Demande demande = new Demande();
        demande.setNumeroRccm("BF-OUA-0123-3673");
        demande.setNumero("RCCM00000K00010L24P");
        demande.setDateImatriculation("2024-10-22");
        demande.setDenominationSociete("Coris Bank");
        demande.setAffected(true);
        demande.setActeDownloaded(false);
        demande.setActeExpired(false);
        demande.setAffectedTo("usertc");

        TypeActeDerive typeActeDerive = new TypeActeDerive();
        typeActeDerive.setId(1L);
        typeActeDerive.setLibelle("Extrait du RCCM");
        typeActeDerive.setStatut(true);
        demande.setTypeActeDerive(typeActeDerive);

        // Set Demandeur
        Demandeur demandeur = new Demandeur();
        demandeur.setNom("koura");
        demandeur.setPrenom("ferdinand");
        demandeur.setTelephone("56070917");
        demandeur.setTypeDemandeur("ADMINISTRATION");
        demande.setDemandeur(demandeur);

        demande.setStatutDemande("NON_PAYE");
        demande.setCreated_date("2024-10-31");

        return demande;
    }

    private File saveFile(MultipartFile file, String label) throws IOException {
        if (file == null) {
            return null;
        }

        String UPLOAD_DIR = "uploads/";
        Path filePath = Paths.get(UPLOAD_DIR, UUID.randomUUID() + "-" + file.getOriginalFilename());
        fileStorageService.saveFile(file.getBytes(), filePath.toString());

        File fileEntity = new File(label, filePath.toString());

        return fileRepository.save(fileEntity);
    }

    public Page<DocumentRequestDto> getPaginatedDocumentRequests(Pageable pageable) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));

        System.out.println(currentUser);
        return documentRequestRepository.findAll(pageable).map(
            documentRequestMapper::toDto
        );
    }

    public DocumentRequestDto getDocumentRequest(String id) {
        return documentRequestRepository.findById(Long.parseLong(id))
            .map(documentRequestMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));
    }
}

