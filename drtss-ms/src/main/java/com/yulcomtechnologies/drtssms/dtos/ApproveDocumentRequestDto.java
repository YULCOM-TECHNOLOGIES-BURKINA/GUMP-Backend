package com.yulcomtechnologies.drtssms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApproveDocumentRequestDto {
    @NotNull
    @NotBlank
    String attestationAnpeNumber;

    @NotNull
    @NotBlank
    String attestationCnssNumber;

    @NotNull
    @NotBlank
    LocalDate attestationAnpeDate;

    @NotNull
    @NotBlank
    LocalDate attestationCnssDate;
}
