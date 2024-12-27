package com.yulcomtechnologies.tresorms.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationConfigRequest {
    @NotNull
    private Integer validityTimeInMonthsForLiquidation;

    @NotNull
    private Integer validityTimeInMonthsForSoumission;

    private String header;

    private String footer;

    @NotNull
    private Integer processingTimeInDaysForLiquidation;

    @NotNull
    private Integer processingTimeInDaysForSoumission;
}
