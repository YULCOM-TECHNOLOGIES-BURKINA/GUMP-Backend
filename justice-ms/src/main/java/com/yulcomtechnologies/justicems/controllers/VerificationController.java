package com.yulcomtechnologies.justicems.controllers;

import com.yulcomtechnologies.justicems.services.justiceClient.JusticeClient;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.VerifyDocumentResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class VerificationController {
    private final JusticeClient justiceClient;

    @GetMapping("verify-document/{number}")
    public ResponseEntity<VerifyDocumentResponseDto> verifyDocument(@PathVariable String number) {
        return ResponseEntity.ok(justiceClient.verifyDocument(number));
    }
}
