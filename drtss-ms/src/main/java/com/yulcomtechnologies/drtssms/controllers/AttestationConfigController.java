package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.*;
import com.yulcomtechnologies.drtssms.services.AttestationConfigService;
import com.yulcomtechnologies.drtssms.services.AttestationGenerator;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@AllArgsConstructor
public class AttestationConfigController {
    private final AttestationConfigService attestationConfigService;
    private final AttestationGenerator attestationGenerator;

    @GetMapping("/attestation-config")
    public List<AttestationConfigDto> getApplicationConfig() {
        return attestationConfigService.getParamsConfigActe();
    }

    @PostMapping(path = "/attestation-params/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ParamsConfigActeDto> updateApplicationConfig(
            @RequestParam("id") Long id,
            @RequestParam("value") String value) {
        try {

            UpdateParamsActeDto updateParamsActeDto = new UpdateParamsActeDto();
            updateParamsActeDto.setId(id);
            updateParamsActeDto.setValue(value);

            ParamsConfigActeDto updatedParam = attestationConfigService.udpateParamActeConfig(updateParamsActeDto);


            return ResponseEntity.ok(updatedParam);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    @PostMapping(path="/attestation-config/update",consumes = {MediaType.ALL_VALUE})
    public ResponseEntity<String> updateAttestationConfig(
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("icone") String icone,
            @RequestPart(value = "logoFile",required = false) MultipartFile logoFile) {
        try {

            UpdateAttestationConfigDto updateAttestationConfigDto=new UpdateAttestationConfigDto();
            updateAttestationConfigDto.setTitle(title);
            updateAttestationConfigDto.setDescription(description);
            updateAttestationConfigDto.setIcone(icone);

             attestationConfigService.UpdateAttestationGlobalInfo(updateAttestationConfigDto, logoFile);
            return ResponseEntity.ok("Attestation configurée avec succès.");
        } catch (Exception e) {
             return ResponseEntity.status(500).body("Erreur lors de la mise à jour de l'attestation : " + e.getMessage());
        }
    }



 @PostMapping(path = "/attestation/test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApproveDocumentRequestDto> updateApplicationConfig(
           @RequestBody ApproveDocumentRequestDto approveDocumentRequestDto
            ) {
        try {
            attestationGenerator.generateDocumentTest(approveDocumentRequestDto);

            return ResponseEntity.status(200).body(new ApproveDocumentRequestDto());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }







}
