package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.Attestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttestationRepository extends JpaRepository<Attestation, Long> {
    Optional<Attestation> findByNumber(String number);

    @Query("SELECT COUNT(a) FROM Attestation a WHERE YEAR(a.createdAt) = ?1")
    Long countByYear(int year);
}
