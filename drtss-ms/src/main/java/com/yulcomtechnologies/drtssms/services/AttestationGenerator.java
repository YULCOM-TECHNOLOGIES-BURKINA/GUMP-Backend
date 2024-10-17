package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.entities.Attestation;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

//@Async
@Service
@AllArgsConstructor
public class AttestationGenerator {
    public static final int VALIDITY_PERIOD_IN_MONTHS = 3;
    private final AttestationRepository attestationRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;

    public void generateDocument(
        ApproveDocumentRequestDto approveDocumentRequestDto,
        Long documentRequestId
    ) {
        var documentRequest = documentRequestRepository.findById(documentRequestId).orElseThrow();

        var file = new File(
            "Attestation",
            "attestations/" + UUID.randomUUID() + ".pdf"
        );

        //Generate and save file
        fileRepository.save(file);

        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(VALIDITY_PERIOD_IN_MONTHS).atTime(23, 59, 59))
            .attestationAnpeNumber(approveDocumentRequestDto.getAttestationAnpeNumber())
            .attestationCnssNumber(approveDocumentRequestDto.getAttestationCnssNumber())
            .attestationAnpeDate(approveDocumentRequestDto.getAttestationAnpeDate())
            .attestationCnssDate(approveDocumentRequestDto.getAttestationCnssDate())
            .documentRequest(DocumentRequest.builder().id(documentRequestId).build())
            .number(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID().toString())
            .documentRequest(documentRequest)
            .file(file)
            .build();

        attestationRepository.save(attestation);
    }
}
