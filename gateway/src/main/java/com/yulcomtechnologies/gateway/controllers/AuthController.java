package com.yulcomtechnologies.gateway.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yulcomtechnologies.gateway.dtos.RegisterRequest;
import com.yulcomtechnologies.gateway.services.KeycloakAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final KeycloakAdminService keycloakAdminService;

    public AuthController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @PostMapping("auth/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest) throws JsonProcessingException {
        return keycloakAdminService.createUser(registerRequest);
    }
}
