package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.tresorms.entities.Attestation;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.entities.File;
import com.yulcomtechnologies.tresorms.repositories.AttestationRepository;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.tresorms.repositories.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    private final TemplateProcessor templateProcessor;
    private final PdfQRCodeService pdfQRCodeService;

    public void generateDocument(
        Long documentRequestId
    ) {
        var documentRequest = documentRequestRepository.findById(documentRequestId).orElseThrow();
        var filePath = "attestations/" + UUID.randomUUID() + ".pdf";

        var file = new File(
            "Attestation",
            filePath
        );

        var map = new HashMap<String, Object>();
        map.put("documentNumber", "0".repeat(4) + documentRequestId);
        map.put("identity", documentRequest.getRequesterId());
        map.put("contractOwner", documentRequest.getRequesterId());
        map.put("address", documentRequest.getAddress());
        map.put("profession", documentRequest.getBusinessDomain());
        map.put("dateCreated", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (documentRequest.getIfuNumber() != null) {
            map.put("ifu", documentRequest.getIfuNumber());
        }

        var filledTemplate = templateProcessor.fillVariables(documentRequest.getRequestType().toString().toLowerCase() + ".html", map);

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), "QR code content");
            fileRepository.save(file);
            fileStorageService.saveFile(fileBytes, filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(VALIDITY_PERIOD_IN_MONTHS).atTime(23, 59, 59))
            .documentRequest(DocumentRequest.builder().id(documentRequestId).build())
            .number(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID().toString())
            .documentRequest(documentRequest)
            .file(file)
            .build();

        attestationRepository.save(attestation);
    }
}
