package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.dtos.ApplicationConfigDto;
import com.yulcomtechnologies.tresorms.dtos.UpdateApplicationConfigRequest;
import com.yulcomtechnologies.tresorms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.tresorms.services.ApplicationConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class ApplicationConfigController {
    private final ApplicationConfigService applicationConfigService;


    @GetMapping("/application-config")
    public ResponseEntity<ApplicationConfigDto> getApplicationConfig() {

        return ResponseEntity.ok(applicationConfigService.getApplicationConfig());
    }

    @PutMapping(path = "/application-config", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updateApplicationConfig(
        @RequestPart(value = "logo", required = false) MultipartFile logoFile,
        @RequestPart @Validated UpdateApplicationConfigRequest updateApplicationConfigRequest
    ) throws IOException {
        applicationConfigService.updateApplicationConfig(updateApplicationConfigRequest, logoFile);

        return ResponseEntity.ok().build();
    }
}
