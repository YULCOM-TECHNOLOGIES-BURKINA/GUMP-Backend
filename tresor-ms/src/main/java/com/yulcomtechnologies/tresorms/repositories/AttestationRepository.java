package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.Attestation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttestationRepository extends JpaRepository<Attestation, Long> {
}
