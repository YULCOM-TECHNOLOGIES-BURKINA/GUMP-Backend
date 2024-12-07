package com.yulcomtechnologies.justicems.repositories;

import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {
    Page<DocumentRequest> findAllByRequesterId(String requesterId, Pageable pageable);
}
