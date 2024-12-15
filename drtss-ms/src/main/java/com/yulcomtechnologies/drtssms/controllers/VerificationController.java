package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.services.VerificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    @GetMapping("verify-document/{number}")
    public ResponseEntity<?> verifyDocument(@PathVariable String number) {
        verificationService.verifyDocument(number);
        return ResponseEntity.ok().build();
    }
}
