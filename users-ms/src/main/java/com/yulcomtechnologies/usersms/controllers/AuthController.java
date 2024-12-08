package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(path = "auth/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> register(
        @RequestPart RegisterRequest registerRequest,
        @RequestPart(value = "cnibFile") MultipartFile cnibFile,
        @RequestPart(value = "statutFile") MultipartFile statutFile
    ) throws Exception {
        authService.register(registerRequest, cnibFile, statutFile);

        return ResponseEntity.ok().build();
    }
}
