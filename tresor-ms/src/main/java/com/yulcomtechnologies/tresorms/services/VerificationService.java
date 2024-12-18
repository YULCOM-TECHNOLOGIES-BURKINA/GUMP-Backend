package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.tresorms.dtos.VerifyDocumentResponseDto;
import com.yulcomtechnologies.tresorms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.tresorms.repositories.AttestationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Service
public class VerificationService {
    private final AttestationRepository attestationRepository;
    private final UsersFeignClient usersFeignClient;

    public VerifyDocumentResponseDto verifyDocument(String number) {
        var attestation = attestationRepository.findByNumber(number).orElseThrow(
            () -> new ResourceNotFoundException("Attestation not found")
        );

        var user = usersFeignClient.getUsernameOrKeycloakId(attestation.getDocumentRequest().getRequesterId());

        return new VerifyDocumentResponseDto(
            attestation.getNumber(),
            user.getCompany().getName(),
            attestation.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            attestation.getExpirationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            attestation.getExpirationDate().isAfter(LocalDateTime.now())
        );
    }
}
