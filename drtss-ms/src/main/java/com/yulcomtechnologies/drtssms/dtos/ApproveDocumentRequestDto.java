package com.yulcomtechnologies.drtssms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
