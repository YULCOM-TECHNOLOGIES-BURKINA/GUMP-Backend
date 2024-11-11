package com.yulcomtechnologies.tresorms.dtos;

import lombok.Data;

@Data
public class UpdateApplicationConfigRequest {
    private Integer validityTimeInMonthsForLiquidation;

    private Integer validityTimeInMonthsForSoumission;

    private String header;

    private String footer;

    private Integer processingTimeInDaysForLiquidation;

    private Integer processingTimeInDaysForSoumission;
}
