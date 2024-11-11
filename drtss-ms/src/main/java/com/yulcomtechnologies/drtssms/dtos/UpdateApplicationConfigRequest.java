package com.yulcomtechnologies.drtssms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UpdateApplicationConfigRequest {
    @NotNull
    private Integer validityTimeInMonths;

    @NotNull
    private Integer processingTimeInDays;

    @NotNull
    @NotBlank
    private String header;

    @NotNull
    @NotBlank
    private String footer;
}
