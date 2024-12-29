package com.yulcomtechnologies.drtssms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "params_config_acte")
public class ParamsConfigActe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "params_config_acte_id")
    private Long id;

 /*   @Column(name = "description")
    private String description;
*/
    @Column(name = "param", nullable = false)
    private String param;

    @Column(name = "labelle", nullable = false)
    private String labelle;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "attestation_config_id", nullable = false)
    private AttestationConfig attestationConfig;
}
