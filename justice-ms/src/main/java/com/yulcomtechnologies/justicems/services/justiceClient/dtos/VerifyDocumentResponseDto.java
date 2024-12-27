package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

public record VerifyDocumentResponseDto(
        String documentNumber,
        String documentOwner,
        String documentGenerationDate,
        String documentExpirationDate,
        Boolean isDocumentValid
) {
}
