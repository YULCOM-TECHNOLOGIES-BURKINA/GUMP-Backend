package com.yulcomtechnologies.drtssms.repositories;

import com.yulcomtechnologies.drtssms.entities.Attestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AttestationRepository extends JpaRepository<Attestation, Long> {
    @Query("SELECT COUNT(a) FROM Attestation a WHERE YEAR(a.createdAt) = ?1")
    Long countByYear(int year);

    Optional<Attestation> findByNumber(String documentNumber);
}
