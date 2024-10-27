package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.exceptions.BadRequestException;
import com.yulcomtechnologies.drtssms.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentRequestService {
    private final DocumentRequestRepository documentRequestRepository;
    private final FileRepository fileRepository;
    private final DocumentRequestMapper documentRequestMapper;
    private final FileStorageService fileStorageService;
    private final AttestationGenerator attestationGenerator;

    public DocumentRequest submitDocumentRequest(MultipartFile attestationCnss, MultipartFile attestationAnpe) throws IOException {
        File idCard = saveFile(attestationCnss, "Attestation CNSS");
        File driverLicense = saveFile(attestationAnpe, "Attestation ANPE");

        var documentRequest = DocumentRequest.builder()
            .requesterId("requesterId")
            .createdAt(LocalDateTime.now())
            .status(DocumentRequestStatus.PENDING.name()).build();

        // Set the files in the document request
        Set<File> files = new HashSet<>();
        files.add(idCard);
        files.add(driverLicense);
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
        return documentRequestRepository.findAll(pageable).map(documentRequestMapper::toDto);
    }

    public DocumentRequestDto getDocumentRequest(String id) {
        return documentRequestRepository.findById(Long.parseLong(id))
            .map(documentRequestMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));
    }

    public void reviewDocumentRequest(Long id, DocumentRequestStatus status) {
        DocumentRequest documentRequest = documentRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("DocumentRequest not found"));

        documentRequest.setStatus(status.name());
        documentRequest.setReviewedBy("KeycloakUserUtil.getCurrentUserId()");

        documentRequestRepository.save(documentRequest);
    }

    public void approveDocumentRequest(Long id, ApproveDocumentRequestDto approveDocumentRequestDto) throws IOException {
        DocumentRequest documentRequest = documentRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("DocumentRequest not found"));

        if (documentRequest.isApproved()) {
            throw new BadRequestException("Document déjà approuvé");
        }

        if (!documentRequest.getIsPaid()) {
            //throw new BadRequestException("Document non payé");
        }

        documentRequest.setStatus(DocumentRequestStatus.APPROVED.name());
        documentRequest.setApprovedBy("KeycloakUserUtil.getCurrentUserId()");

        attestationGenerator.generateDocument(
            approveDocumentRequestDto,
            documentRequest.getId()
        );

        documentRequestRepository.save(documentRequest);

    }
}

