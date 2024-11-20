package com.yulcomtechnologies.drtssms.repositories;

import com.yulcomtechnologies.drtssms.entities.Attestation;
import com.yulcomtechnologies.drtssms.entities.SignatureCertificat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureCertificatRepository extends JpaRepository<SignatureCertificat, Long> {
}
