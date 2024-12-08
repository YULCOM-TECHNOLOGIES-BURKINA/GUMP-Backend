package com.yulcomtechnologies.drtssms.repositories;

import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
    Page<DocumentRequest> findAllByRequesterId(String keycloakUserId, Pageable pageable);

    Page<DocumentRequest> findAllByRegion(String region, Pageable pageable);
}
