package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.entities.Attestation;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.sharedlibrary.services.PdfQRCodeService;
import com.yulcomtechnologies.sharedlibrary.services.TemplateProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.UUID;

//@Async
@Service
@AllArgsConstructor
public class AttestationGenerator {
    private final AttestationRepository attestationRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final FileStorageService fileStorageService;
    private final FileRepository fileRepository;
    private final TemplateProcessor templateProcessor;
    private final PdfQRCodeService pdfQRCodeService;
    private final ApplicationConfigRepository applicationConfigRepository;

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
        map.put("attestationNumber", approveDocumentRequestDto.getAttestationAnpeNumber());
        map.put("anpeNumber", approveDocumentRequestDto.getAttestationAnpeNumber());
        map.put("cnssNumber", approveDocumentRequestDto.getAttestationCnssNumber());
        map.put("cnssDate", approveDocumentRequestDto.getAttestationCnssDate());
        map.put("anpeDate", approveDocumentRequestDto.getAttestationAnpeDate());
        map.put("companyName", "Yulcom technologies");
        map.put("location", "Ouagadougou");
        map.put("address", "Ouaga 2000");
        map.put("bp", "Yulcom technologies");
        map.put("region", "Centre");
        map.put("logo", fileStorageService.getPath(applicationConfigRepository.get().getLogo()));
        map.put("telephone", "50-50-50-50");


        var filledTemplate = templateProcessor.fillVariables("attestation.html", map);

        try {
            var fileBytes = pdfQRCodeService.addQRCodeToPDF(templateProcessor.htmlToPdf(filledTemplate), "QR code content");
            fileRepository.save(file);
            fileStorageService.saveFile(fileBytes, filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var attestation = Attestation.builder()
            .expirationDate(LocalDate.now().plusMonths(
                applicationConfigRepository.get().getValidityTimeInMonths()
            ).atTime(23, 59, 59))
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
