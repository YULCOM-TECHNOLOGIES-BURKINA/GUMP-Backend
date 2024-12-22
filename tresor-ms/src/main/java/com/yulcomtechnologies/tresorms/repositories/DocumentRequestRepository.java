package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
    Page<DocumentRequest> findByRequesterId(String requesterId, Pageable pageable);
    Page<DocumentRequest> findByPublicContractNumber(String requesterId, String publicContractNumber, Pageable pageable);
    Page<DocumentRequest> findByRequesterIdAndPublicContractNumber(String requesterId, String publicContractNumber, Pageable pageable);
}
