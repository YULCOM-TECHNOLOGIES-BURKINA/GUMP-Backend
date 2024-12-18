package com.yulcomtechnologies.drtssms.dtos;

public record VerifyDocumentResponseDto(
        String documentNumber,
        String documentOwner,
        String documentGenerationDate,
        String documentExpirationDate,
        Boolean isDocumentValid
) {
}
