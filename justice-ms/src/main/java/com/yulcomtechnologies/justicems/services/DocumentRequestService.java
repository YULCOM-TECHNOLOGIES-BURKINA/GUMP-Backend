package com.yulcomtechnologies.justicems.services;

import com.yulcomtechnologies.feignClients.UserMsFeignClient;
import com.yulcomtechnologies.feignClients.dtos.UserDto;
import com.yulcomtechnologies.justicems.dtos.DocumentRequestDto;
import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import com.yulcomtechnologies.justicems.entities.File;
import com.yulcomtechnologies.justicems.enums.TypeDemandeEnum;
import com.yulcomtechnologies.justicems.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.justicems.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.justicems.repositories.FileRepository;
import com.yulcomtechnologies.justicems.services.justiceClient.JusticeClient;
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
import java.time.format.DateTimeFormatter;
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
    private final JusticeClient justiceClient;

    public DocumentRequest submitDocumentRequest(MultipartFile extraitRccm, MultipartFile statutEntreprise, LocalDate immatriculationDate, TypeDemandeEnum typeDemande) throws IOException {
        //Call e-service here
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));
        var currentUserInfo = userMsFeignClient.getUser(currentUser.getKeycloakUserId());

        var demandeJustice = justiceClient.createDemandeWithFiles(createDemande(extraitRccm, statutEntreprise, immatriculationDate, typeDemande, currentUserInfo), extraitRccm, statutEntreprise).getBody();

        log.info("Demande justice: {}", demandeJustice);
        File extraitRccmDocument = saveFile(extraitRccm, "Recepissé RCCM");
        File statutEntrepriseDocument = saveFile(statutEntreprise, "Statut Entreprise");

        assert demandeJustice != null;

        var documentRequest = DocumentRequest.builder()
            .requesterId(currentUser.getKeycloakUserId())
            .isPaid(false)
            .number(demandeJustice.numero())
            .externalId(demandeJustice.id())
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
        documentRequest.setType(typeDemande.name());
        documentRequest.setCompanyName(currentUserInfo.getCompany().getName());
        documentRequest.setRccm(currentUserInfo.getCompany().getRccm());

        return documentRequestRepository.save(documentRequest);
    }

    private Demande createDemande(
        MultipartFile extraitRccm,
        MultipartFile statutEntreprise,
        LocalDate immatriculationDate,
        TypeDemandeEnum typeDemande,
        UserDto currentUserInfo
    ) {
        Demande demande = new Demande();
        demande.setCreated_date(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        var company = currentUserInfo.getCompany();

        demande.setNumeroRccm(company.getRccm());
        demande.setDateImatriculation("2024-10-22");
        demande.setDenominationSociete(company.getName());

        TypeActeDerive typeActeDerive = new TypeActeDerive();

        if (typeDemande == TypeDemandeEnum.EXTRAIT_RCCM) {
            typeActeDerive.setId(1L);
            typeActeDerive.setLibelle("Extrait du RCCM");
            typeActeDerive.setStatut(true);
        } else if (typeDemande == TypeDemandeEnum.CERTIFICAT_NON_FAILLITE) {
            typeActeDerive.setId(2L);
            typeActeDerive.setLibelle("Attestation de non faillite");
            typeActeDerive.setStatut(true);
        }

        demande.setTypeActeDerive(typeActeDerive);
        // Set Demandeur
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(company.getName());
        demandeur.setPrenom(company.getName());
        demandeur.setTypeDemandeur("ADMINISTRATION");
        demande.setDemandeur(demandeur);

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

        return documentRequestRepository.findAllByRequesterId(currentUser.getKeycloakUserId(), pageable).map(
            documentRequestMapper::toDto
        );
    }

    public DocumentRequestDto getDocumentRequest(String id) {
        return documentRequestRepository.findById(Long.parseLong(id))
            .map(documentRequestMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));
    }
}

