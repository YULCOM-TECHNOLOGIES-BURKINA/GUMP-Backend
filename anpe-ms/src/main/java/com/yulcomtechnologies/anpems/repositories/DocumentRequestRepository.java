package com.yulcomtechnologies.anpems.repositories;
 import com.yulcomtechnologies.anpems.entities.DocumentRequest;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRequestRepository extends JpaRepository<DocumentRequest, Long> {

}
