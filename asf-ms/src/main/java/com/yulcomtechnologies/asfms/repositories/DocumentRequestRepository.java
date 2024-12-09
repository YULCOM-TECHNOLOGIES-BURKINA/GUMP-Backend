package com.yulcomtechnologies.asfms.repositories;
import com.yulcomtechnologies.asfms.entities.DocumentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {

}
