package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.services.VerificationService;
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
        return ResponseEntity.ok(verificationService.verifyDocument(number));
    }
}