package com.yulcomtechnologies.drtssms.repositories;

import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignatureScannerRepository extends JpaRepository<SignatureScanner,Long> {
}
