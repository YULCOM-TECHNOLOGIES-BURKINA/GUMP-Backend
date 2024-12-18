package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.Attestation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttestationRepository extends JpaRepository<Attestation, Long> {
    Optional<Attestation> findByNumber(String number);
}
