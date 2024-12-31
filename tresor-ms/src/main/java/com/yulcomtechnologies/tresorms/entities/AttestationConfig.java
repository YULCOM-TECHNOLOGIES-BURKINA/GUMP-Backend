package com.yulcomtechnologies.tresorms.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "attestation_config")
public class AttestationConfig {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attestation_config_id")
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "logo", nullable = false)
    private String logo;

    @Column(name = "icone", nullable = false)
    private String icone;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "description")
    private String description;



    @OneToMany(mappedBy = "attestationConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParamsConfigActe> acteConfig;

}
