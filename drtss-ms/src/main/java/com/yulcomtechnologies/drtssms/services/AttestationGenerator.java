package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.entities.Attestation;
import com.yulcomtechnologies.drtssms.entities.AttestationConfig;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
 import com.yulcomtechnologies.sharedlibrary.services.TemplateProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

//@Async
@Service
public class AttestationGenerator {
    private final AttestationRepository attestationRepository;
    private final AttestationConfigService attestationConfigService;
    private final DocumentRequestRepository documentRequestRepository;
    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;
    private final TemplateProcessor templateProcessor;
    private final PdfQrCodeService pdfQRCodeService;
    private final ApplicationConfigRepository applicationConfigRepository;
    private final UsersFeignClient usersFeignClient;
    private final NumberGeneratorService numberGeneratorService;
    private final String appFrontUrl;


    public AttestationGenerator(AttestationConfigService attestationConfigService, AttestationRepository attestationRepository, DocumentRequestRepository documentRequestRepository, FileStorageService fileStorageService, FileRepository fileRepository, TemplateProcessor templateProcessor, PdfQrCodeService pdfQRCodeService, ApplicationConfigRepository applicationConfigRepository, UsersFeignClient usersFeignClient, NumberGeneratorService numberGeneratorService,
                                @Value("${app.url}") String appFrontUrl
    ) {
        this.attestationRepository = attestationRepository;
        this.documentRequestRepository = documentRequestRepository;
        this.fileStorageService = fileStorageService;
        this.fileRepository = fileRepository;
        this.templateProcessor = templateProcessor;
        this.pdfQRCodeService = pdfQRCodeService;
        this.applicationConfigRepository = applicationConfigRepository;
        this.usersFeignClient = usersFeignClient;
        this.numberGeneratorService = numberGeneratorService;
        this.appFrontUrl = appFrontUrl;
        this.attestationConfigService = attestationConfigService;

    }

    public void generateDocument(
        ApproveDocumentRequestDto approveDocumentRequestDto,
        Long documentRequestId
    ) {
        var documentRequest = documentRequestRepository.findById(documentRequestId).orElseThrow();
        var filePath = "attestations/" + UUID.randomUUID() + ".pdf";

        var file = new File(
            "Attestation",
            filePath
        );

        var map = new HashMap<String, Object>();
        var userData = usersFeignClient.getUsernameOrKeycloakId(documentRequest.getRequesterId());
        var company = userData.getCompany();
        fileRepository.save(file);

        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(
                applicationConfigRepository.get().getValidityTimeInMonths()
            ).atTime(23, 59, 59))
            .attestationAnpeNumber(approveDocumentRequestDto.getAttestationAnpeNumber())
            .attestationCnssNumber(approveDocumentRequestDto.getAttestationCnssNumber())
            .attestationAnpeDate(approveDocumentRequestDto.getAttestationAnpeDate())
            .attestationCnssDate(approveDocumentRequestDto.getAttestationCnssDate())
            .documentRequest(DocumentRequest.builder().id(documentRequestId).build())
            .number(numberGeneratorService.generateNumber())
            .createdAt(LocalDateTime.now())
            .uuid(UUID.randomUUID().toString())
            .documentRequest(documentRequest)
            .file(file)
            .build();


        attestationRepository.save(attestation);
      AttestationConfig drtpsAttestation= attestationConfigService.getAttestationConfig();
        map.put("attestationNumber", attestation.getNumber());
        map.put("anpeNumber", approveDocumentRequestDto.getAttestationAnpeNumber());
        map.put("cnssNumber", approveDocumentRequestDto.getAttestationCnssNumber());
        map.put("cnssDate", approveDocumentRequestDto.getAttestationCnssDate());
        map.put("anpeDate", approveDocumentRequestDto.getAttestationAnpeDate());
        map.put("companyName", company.getName());
        map.put("location", company.getLocation());
        map.put("address", company.getAddress());
        map.put("bp", company.getPostalAddress());
        map.put("region", userData.getRegion().replace("_", ""));
        map.put("logo", fileStorageService.getPath(applicationConfigRepository.get().getLogo()));
        map.put("telephone", company.getPhone());

        map.put("title",drtpsAttestation.getTitle());

        /************
         * New Params
         */
        map.put("title",drtpsAttestation.getTitle());
        map.put("validiteMois",attestationConfigService.findByLabelle("validiteMois").get(0).getValue());
        map.put("validiteJours",attestationConfigService.findByLabelle("validiteJours").get(0).getValue());
        map.put("delaiTraitement",attestationConfigService.findByLabelle("delaiTraitement").get(0).getValue());

        map.put("prixActe",attestationConfigService.findByLabelle("prixActe").get(0).getValue());
        map.put("intitule",attestationConfigService.findByLabelle("intitule").get(0).getValue());
        map.put("ministaire",attestationConfigService.findByLabelle("ministaire").get(0).getValue());
        map.put("devise",attestationConfigService.findByLabelle("devise").get(0).getValue());
        map.put("titreSignataire",attestationConfigService.findByLabelle("titreSignataire").get(0).getValue());
        map.put("adressEmetrice",attestationConfigService.findByLabelle("adressEmetrice").get(0).getValue());
        map.put("contactEmetrice",attestationConfigService.findByLabelle("contactEmetrice").get(0).getValue());
        map.put("vu1",attestationConfigService.findByLabelle("vu1").get(0).getValue());
        map.put("vu2",attestationConfigService.findByLabelle("vu2").get(0).getValue());
        map.put("logo",attestationConfigService.getAttestationConfig().getLogo());


        var filledTemplate = templateProcessor.fillVariables("attestation.html", map);

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), appFrontUrl + "/api/verify-document/" + attestation.getNumber() + "/public?service=drtss-ms");
            fileStorageService.saveFile(fileBytes, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void generateDocumentTest(
            ApproveDocumentRequestDto approveDocumentRequestDto
    ) {
      //  var documentRequest = documentRequestRepository.findById(documentRequestId).orElseThrow();
        var filePath = "attestations/" + UUID.randomUUID() + "MCT.pdf";

        var file = new File(
                "Attestation",
                filePath
        );
        System.out.println(filePath);
        var map = new HashMap<String, Object>();
       // var userData = usersFeignClient.getUsernameOrKeycloakId(documentRequest.getRequesterId());
      //  var company = userData.getCompany();
        fileRepository.save(file);

      /*  var attestation = Attestation.builder()
                .expirationDate(LocalDate.now().plusMonths(
                        applicationConfigRepository.get().getValidityTimeInMonths()
                ).atTime(23, 59, 59))
                .attestationAnpeNumber(approveDocumentRequestDto.getAttestationAnpeNumber())
                .attestationCnssNumber(approveDocumentRequestDto.getAttestationCnssNumber())
                .attestationAnpeDate(approveDocumentRequestDto.getAttestationAnpeDate())
                .attestationCnssDate(approveDocumentRequestDto.getAttestationCnssDate())
                .documentRequest(DocumentRequest.builder().id(documentRequestId).build())
                .number(numberGeneratorService.generateNumber())
                .createdAt(LocalDateTime.now())
                .uuid(UUID.randomUUID().toString())
                .documentRequest(documentRequest)
                .file(file)
                .build();


        attestationRepository.save(attestation);*/
        AttestationConfig drtpsAttestation= attestationConfigService.getAttestationConfig();
     //   List<ParamsConfigActeDto> validiteMois=attestationConfigService.findByLabelle("validiteMois").get(0).getValue();

        map.put("attestationNumber", 995);
        map.put("anpeNumber", approveDocumentRequestDto.getAttestationAnpeNumber());
        map.put("cnssNumber", approveDocumentRequestDto.getAttestationCnssNumber());
        map.put("cnssDate", approveDocumentRequestDto.getAttestationCnssDate());
        map.put("anpeDate", approveDocumentRequestDto.getAttestationAnpeDate());
        map.put("companyName", "BOBO SARL TEST");
        map.put("location","BOBO-LOCATION");
        map.put("address", "BOBO-ADDRESS");
        map.put("bp", "BOITE P");
        map.put("region", "CENTRE EST");
        map.put("logo", fileStorageService.getPath(applicationConfigRepository.get().getLogo()));
        map.put("telephone", 75401144);


        /************
         * New Params
         */
        map.put("title",drtpsAttestation.getTitle());
        map.put("validiteMois",attestationConfigService.findByLabelle("validiteMois").get(0).getValue());
        map.put("validiteJours",attestationConfigService.findByLabelle("validiteJours").get(0).getValue());
        map.put("delaiTraitement",attestationConfigService.findByLabelle("delaiTraitement").get(0).getValue());

        map.put("prixActe",attestationConfigService.findByLabelle("prixActe").get(0).getValue());
        map.put("intitule",attestationConfigService.findByLabelle("intitule").get(0).getValue());
        map.put("ministaire",attestationConfigService.findByLabelle("ministaire").get(0).getValue());
        map.put("devise",attestationConfigService.findByLabelle("devise").get(0).getValue());
        map.put("titreSignataire",attestationConfigService.findByLabelle("titreSignataire").get(0).getValue());
        map.put("adressEmetrice",attestationConfigService.findByLabelle("adressEmetrice").get(0).getValue());
        map.put("contactEmetrice",attestationConfigService.findByLabelle("contactEmetrice").get(0).getValue());
        map.put("vu1",attestationConfigService.findByLabelle("vu1").get(0).getValue());
        map.put("vu2",attestationConfigService.findByLabelle("vu2").get(0).getValue());
        map.put("logo",attestationConfigService.getAttestationConfig().getLogo());

        var filledTemplate = templateProcessor.fillVariables("attestation.html", map);

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), appFrontUrl + "/api/verify-document/" + 995+ "/public?service=drtss-ms");
            fileStorageService.saveFile(fileBytes, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}
