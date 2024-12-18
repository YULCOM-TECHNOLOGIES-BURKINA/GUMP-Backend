package com.yulcomtechnologies.tresorms.dtos;

public record VerifyDocumentResponseDto(
        String documentNumber,
        String documentOwner,
        String documentGenerationDate,
        String documentExpirationDate,
        Boolean isDocumentValid
) {
}
