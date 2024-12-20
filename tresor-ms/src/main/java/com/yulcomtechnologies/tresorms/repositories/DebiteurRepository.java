package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DebiteurRepository extends JpaRepository<DebiteurEntity, Long> {
    Optional<DebiteurEntity> findByNumeroIFU(String numeroIFU);

    @Query("SELECT SUM(d.montantDu) FROM DebiteurEntity d WHERE d.numeroIFU = :numeroIFU")
    Double findTotalDebtByIfu(String numeroIFU);
}
