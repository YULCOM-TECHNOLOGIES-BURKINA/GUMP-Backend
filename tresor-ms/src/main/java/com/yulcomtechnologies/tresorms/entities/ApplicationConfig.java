package com.yulcomtechnologies.tresorms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "logo_id")
    private File logo;

    @Column(name = "validity_time_in_months_for_liquidation")
    private Integer validityTimeInMonthsForLiquidation;

    @Column(name = "validity_time_in_months_for_soumission")
    private Integer validityTimeInMonthsForSoumission;

    @Column(columnDefinition = "TEXT")
    private String header;

    @Column(columnDefinition = "TEXT")
    private String footer;

    @Column(name = "processing_time_in_days_for_liquidation", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer processingTimeInDaysForLiquidation = 1;

    @Column(name = "processing_time_in_days_for_soumission", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer processingTimeInDaysForSoumission = 1;
}

