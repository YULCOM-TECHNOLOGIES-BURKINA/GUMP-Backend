package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.sharedlibrary.services.PdfQRCodeService;
import com.yulcomtechnologies.sharedlibrary.services.TemplateProcessor;
import com.yulcomtechnologies.tresorms.entities.Attestation;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.entities.File;
import com.yulcomtechnologies.tresorms.enums.RequestType;
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
    private final PdfFiligraneService pdfFiligraneService;

    public void generateDocument(
        Long documentRequestId
    ) {
        var documentRequest = documentRequestRepository.findById(documentRequestId).orElseThrow();
        var filePath = "attestations/" + UUID.randomUUID() + ".pdf";

        var file = new File(
            "Attestation",
            filePath
        );

        fileRepository.save(file);


        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(VALIDITY_PERIOD_IN_MONTHS).atTime(23, 59, 59))
            .documentRequest(documentRequest)
            .number(UUID.randomUUID().toString())
            .uuid(UUID.randomUUID().toString())
            .documentRequest(documentRequest)
            .file(file)
            .build();

        attestationRepository.save(attestation);

        var map = new HashMap<String, Object>();
        map.put("documentNumber", attestation.getNumber());
        map.put("identity", documentRequest.getOrganizationName());
        map.put("contractOwner", documentRequest.getContractingOrganizationName());
        map.put("address", documentRequest.getAddress());
        map.put("profession", documentRequest.getBusinessDomain());
        map.put("dateCreated", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (documentRequest.getIfuNumber() != null) {
            map.put("ifu", documentRequest.getIfuNumber());
        }

        var filledTemplate = templateProcessor.fillVariables(documentRequest.getRequestType().toString().toLowerCase() + ".html", map);
        var typeResquest= documentRequest.getRequestType().toString();

        try {


            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), "QR code content");
            var filigraneTexte="Agence judiciaire de l'Etat - Valable pour (03 mois)";

            if (typeResquest.equals(RequestType.LIQUIDATION.name())){
                filigraneTexte="Agence judiciaire de l'Etat - Valable pour (01 mois)";
            }

            var finalFileBytes = pdfFiligraneService.addFiligraneToPDF(fileBytes,filigraneTexte);

            fileStorageService.saveFile(finalFileBytes, filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
