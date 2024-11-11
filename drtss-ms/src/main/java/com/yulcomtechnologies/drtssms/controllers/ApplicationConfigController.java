package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.ApplicationConfigDto;
import com.yulcomtechnologies.drtssms.dtos.UpdateApplicationConfigRequest;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.drtssms.services.ApplicationConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class ApplicationConfigController {
    private final ApplicationConfigService applicationConfigService;
    private final ApplicationConfigRepository applicationConfigRepository;


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
