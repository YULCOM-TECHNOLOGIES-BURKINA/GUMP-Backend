package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DebiteurRepository extends JpaRepository<DebiteurEntity, Long> {
    Optional<DebiteurEntity> findByNumeroIFU(String numeroIFU);
}
