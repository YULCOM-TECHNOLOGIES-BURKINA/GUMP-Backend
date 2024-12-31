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

        var validiteSoumission = attestationConfigService.findByLabelle("validiteSoumission").get(0).getValue();
        var validiteLiquidation = attestationConfigService.findByLabelle("validiteLiquidation").get(0).getValue();

        var validiteAttestation = documentRequest.getRequestType().equals(RequestType.LIQUIDATION) ? validiteLiquidation : validiteSoumission;

        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(Long.parseLong(validiteAttestation)).atTime(23, 59, 59))
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

        AttestationConfig ajeAttestation= attestationConfigService.getAttestationConfig();
        map.put("title",ajeAttestation.getTitle());
        map.put("logo",attestationConfigService.getAttestationConfig().getLogo());
        map.put("devise",attestationConfigService.findByLabelle("devise").get(0).getValue());
        map.put("ministaire",attestationConfigService.findByLabelle("ministaire").get(0).getValue());
        map.put("adressEmetrice",attestationConfigService.findByLabelle("adressEmetrice").get(0).getValue());
        map.put("contactEmetrice",attestationConfigService.findByLabelle("contactEmetrice").get(0).getValue());
        map.put("validiteSoumission",attestationConfigService.findByLabelle("validiteSoumission").get(0).getValue());
        map.put("validiteLiquidation",attestationConfigService.findByLabelle("validiteLiquidation").get(0).getValue());
        map.put("libelleDeNonCreance",attestationConfigService.findByLabelle("libelleDeNonCreance").get(0).getValue());


        if (documentRequest.getIfuNumber() != null) {
            map.put("ifu", documentRequest.getIfuNumber());
        }

        var filledTemplate = templateProcessor.fillVariables(documentRequest.getRequestType().toString().toLowerCase() + ".html", map);
        var typeResquest= documentRequest.getRequestType().toString();

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), appFrontUrl + "/api/verify-document/" + attestation.getNumber() + "/public?service=tresor-ms");
            var filigraneTexte="Agence judiciaire de l'Etat - Valable pour("+attestationConfigService.findByLabelle("validiteSoumission").get(0).getValue()+"mois)";

            if (typeResquest.equals(RequestType.LIQUIDATION.name())){
                filigraneTexte="Agence judiciaire de l'Etat - Valable pour ("+attestationConfigService.findByLabelle("validiteLiquidation").get(0).getValue()+"mois)";
            }

            var finalFileBytes = pdfFiligraneService.addFiligraneToPDF(fileBytes,filigraneTexte);

            fileStorageService.saveFile(finalFileBytes, filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
