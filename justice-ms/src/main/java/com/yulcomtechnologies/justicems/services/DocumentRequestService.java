package com.yulcomtechnologies.justicems.services;

import com.yulcomtechnologies.justicems.dtos.DocumentRequestDto;
import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import com.yulcomtechnologies.justicems.entities.File;
import com.yulcomtechnologies.justicems.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.justicems.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.justicems.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
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
import java.time.LocalDateTime;
import java.util.Base64;
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
    private final EventPublisher eventPublisher;
    private final AuthenticatedUserService authenticatedUserService;

    public DocumentRequest submitDocumentRequest(MultipartFile attestationCnss, MultipartFile attestationAnpe, String publicContractNumber) throws IOException {
        File cnssAttestation = saveFile(attestationCnss, "Attestation CNSS");
        File anpeAttestation = saveFile(attestationAnpe, "Attestation ANPE");

        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));

        var documentRequest = DocumentRequest.builder()
            .requesterId(currentUser.getKeycloakUserId())
            .createdAt(LocalDateTime.now())
            .status("PENDING").build();

        // Set the files in the document request
        Set<File> files = new HashSet<>();
        files.add(cnssAttestation);
        files.add(anpeAttestation);
        documentRequest.setFiles(files);

        return documentRequestRepository.save(documentRequest);
    }

    private File saveFile(MultipartFile file, String label) throws IOException {
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

