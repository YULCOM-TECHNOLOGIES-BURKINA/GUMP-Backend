package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.sharedlibrary.services.PdfQRCodeService;
import com.yulcomtechnologies.sharedlibrary.services.TemplateProcessor;
import com.yulcomtechnologies.tresorms.entities.Attestation;
import com.yulcomtechnologies.tresorms.entities.AttestationConfig;
import com.yulcomtechnologies.tresorms.entities.File;
import com.yulcomtechnologies.tresorms.enums.RequestType;
import com.yulcomtechnologies.tresorms.repositories.AttestationRepository;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.tresorms.repositories.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

//@Async
@Service
@Slf4j
public class AttestationGenerator {
    public static final int VALIDITY_PERIOD_IN_MONTHS = 3;
    private final AttestationRepository attestationRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;
    private final TemplateProcessor templateProcessor;
    private final PdfQRCodeService pdfQRCodeService;
    private final PdfFiligraneService pdfFiligraneService;
    private final NumberGeneratorService numberGeneratorService;
    private final String appFrontUrl;
    private final AttestationConfigService attestationConfigService;

    public AttestationGenerator(AttestationConfigService attestationConfigService, AttestationRepository attestationRepository, DocumentRequestRepository documentRequestRepository, FileStorageService fileStorageService, FileRepository fileRepository, TemplateProcessor templateProcessor, PdfQRCodeService pdfQRCodeService, PdfFiligraneService pdfFiligraneService, NumberGeneratorService numberGeneratorService, @Value("${app.url}") String appFrontUrl) {
        this.attestationRepository = attestationRepository;
        this.documentRequestRepository = documentRequestRepository;
        this.fileStorageService = fileStorageService;
        this.fileRepository = fileRepository;
        this.templateProcessor = templateProcessor;
        this.pdfQRCodeService = pdfQRCodeService;
        this.pdfFiligraneService = pdfFiligraneService;
        this.numberGeneratorService = numberGeneratorService;
        this.appFrontUrl = appFrontUrl;
        this.attestationConfigService = attestationConfigService;
    }

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

        var number = numberGeneratorService.generateNumber();

        log.info("Generated number: {}", number);

        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(VALIDITY_PERIOD_IN_MONTHS).atTime(23, 59, 59))
            .documentRequest(documentRequest)
            .number(number)
            .uuid(UUID.randomUUID().toString())
            .documentRequest(documentRequest)
            .createdAt(LocalDateTime.now())
            .file(file)
            .build();

        attestationRepository.save(attestation);

        var map = new HashMap<String, Object>();
        map.put("documentNumber", number);
        map.put("identity", documentRequest.getOrganizationName());
        map.put("contractOwner", documentRequest.getContractingOrganizationName());
        map.put("address", documentRequest.getAddress());
        map.put("profession", documentRequest.getBusinessDomain());
        map.put("dateCreated", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        /************
         * New Params
         */
        AttestationConfig ajeAttestation= attestationConfigService.getAttestationConfig();
        map.put("title",ajeAttestation.getTitle());
        map.put("logo",attestationConfigService.getAttestationConfig().getLogo());
        map.put("devise",attestationConfigService.findByLabelle("devise").get(0).getValue());

        if (documentRequest.getIfuNumber() != null) {
            map.put("ifu", documentRequest.getIfuNumber());
        }

        var filledTemplate = templateProcessor.fillVariables(documentRequest.getRequestType().toString().toLowerCase() + ".html", map);
        var typeResquest= documentRequest.getRequestType().toString();

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), appFrontUrl + "/api/verify-document/" + attestation.getNumber() + "/public?service=tresor-ms");
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


    public void generateDocumentTest(
    ) {
     var documentRequest = documentRequestRepository.findById(31L).orElseThrow();
        var filePath = "attestations/" + UUID.randomUUID() + ".pdf";

        var file = new File(
                "Attestation",
                filePath
        );

        fileRepository.save(file);

        var number = numberGeneratorService.generateNumber();

        log.info("Generated number: {}", number);

        var attestation = Attestation.builder()
                .expirationDate(LocalDate.now().plusMonths(VALIDITY_PERIOD_IN_MONTHS).atTime(23, 59, 59))
                .documentRequest(documentRequest)
                .number(number)
                .uuid(UUID.randomUUID().toString())
                .documentRequest(documentRequest)
                .createdAt(LocalDateTime.now())
                .file(file)
                .build();

        attestationRepository.save(attestation);
        var map = new HashMap<String, Object>();
        map.put("documentNumber", number);
        map.put("identity", documentRequest.getOrganizationName());
        map.put("contractOwner", documentRequest.getContractingOrganizationName());
        map.put("address", documentRequest.getAddress());
        map.put("profession", documentRequest.getBusinessDomain());
        map.put("dateCreated", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));



        map.put("ifu", "1000002A");


        /************
         * New Params
         */
        AttestationConfig ajeAttestation= attestationConfigService.getAttestationConfig();
        map.put("title",ajeAttestation.getTitle());
        map.put("logo",attestationConfigService.getAttestationConfig().getLogo());
        map.put("devise",attestationConfigService.findByLabelle("devise").get(0).getValue());

     /*   map.put("validiteMois",attestationConfigService.findByLabelle("validiteMois").get(0).getValue());
        map.put("validiteJours",attestationConfigService.findByLabelle("validiteJours").get(0).getValue());
        map.put("delaiTraitement",attestationConfigService.findByLabelle("delaiTraitement").get(0).getValue());

        map.put("prixActe",attestationConfigService.findByLabelle("prixActe").get(0).getValue());
        map.put("intitule",attestationConfigService.findByLabelle("intitule").get(0).getValue());
        map.put("ministaire",attestationConfigService.findByLabelle("ministaire").get(0).getValue());
        map.put("titreSignataire",attestationConfigService.findByLabelle("titreSignataire").get(0).getValue());
        map.put("adressEmetrice",attestationConfigService.findByLabelle("adressEmetrice").get(0).getValue());
        map.put("contactEmetrice",attestationConfigService.findByLabelle("contactEmetrice").get(0).getValue());
        map.put("vu1",attestationConfigService.findByLabelle("vu1").get(0).getValue());
        map.put("vu2",attestationConfigService.findByLabelle("vu2").get(0).getValue());*/



        var filledTemplate = templateProcessor.fillVariables("liquidation.html", map);
        var typeResquest= "LIQUIDATION";

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), appFrontUrl + "/api/verify-document/" + attestation.getNumber() + "/public?service=tresor-ms");
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
