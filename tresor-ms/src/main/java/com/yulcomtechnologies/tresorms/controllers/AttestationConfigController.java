package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.dtos.AttestationConfigDto;
import com.yulcomtechnologies.tresorms.dtos.ParamsConfigActeDto;
import com.yulcomtechnologies.tresorms.dtos.UpdateAttestationConfigDto;
import com.yulcomtechnologies.tresorms.dtos.UpdateParamsActeDto;
import com.yulcomtechnologies.tresorms.services.AttestationConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@AllArgsConstructor
public class AttestationConfigController {
    private final AttestationConfigService attestationConfigService;

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
}
