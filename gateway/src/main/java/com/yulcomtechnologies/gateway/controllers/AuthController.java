package com.yulcomtechnologies.gateway.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yulcomtechnologies.gateway.dtos.RegisterRequest;
import com.yulcomtechnologies.gateway.services.CorporationInfosExtractor;
import com.yulcomtechnologies.gateway.services.KeycloakAdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final KeycloakAdminService keycloakAdminService;
    private final CorporationInfosExtractor corporationInfosExtractor;

    @PostMapping("auth/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest) throws JsonProcessingException {
        return keycloakAdminService.createUser(registerRequest);
    }

    @GetMapping("test")
    public ResponseEntity<?> health() {
        corporationInfosExtractor.extractCorporationInfos("00151072N");
        return ResponseEntity.ok().build();
    }
}
