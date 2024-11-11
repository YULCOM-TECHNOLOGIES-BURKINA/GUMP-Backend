package com.yulcomtechnologies.drtssms.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "application_config")
public class ApplicationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "logo_id", referencedColumnName = "id")
    private File logo;

    @Column(name = "validity_time_in_months", length = 255)
    private Integer validityTimeInMonths;

    @Column(name = "header", columnDefinition = "TEXT")
    private String header;

    @Column(name = "footer", columnDefinition = "TEXT")
    private String footer;
}

