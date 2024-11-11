package com.yulcomtechnologies.tresorms.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationConfigDto {
    private String logo;

    private Integer validityTimeInMonthsForLiquidation;

    private Integer validityTimeInMonthsForSoumission;

    private String header;

    private String footer;

    private Integer processingTimeInDaysForLiquidation;

    private Integer processingTimeInDaysForSoumission;
}
