package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.VerifyDocumentResponseDto;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.AttestationRepository;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
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
            () -> new ResourceNotFoundException("Attestation inexistante, ce document n'est pas valide")
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
