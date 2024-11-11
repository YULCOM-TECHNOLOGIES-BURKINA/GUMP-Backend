package com.yulcomtechnologies.drtssms.dtos;

import com.yulcomtechnologies.drtssms.entities.File;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationConfigDto {
    private String logo;

    private Integer validityTimeInMonths;

    private Integer processingTimeInDays;

    private String header;

    private String footer;
}
