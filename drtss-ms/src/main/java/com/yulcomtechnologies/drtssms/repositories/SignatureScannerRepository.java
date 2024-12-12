package com.yulcomtechnologies.drtssms.repositories;

import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SignatureScannerRepository extends JpaRepository<SignatureScanner,Long> {

    @Query("from SignatureScanner s where s.user_id =:userId")
    Optional< SignatureScanner> findSignatureScannerByUserId(@Param("userId") Long userId);

    @Query("from SignatureScanner s where s.email =:email")
    Optional< SignatureScanner> getSignatoryByEmail(@Param("email") String email);

}
