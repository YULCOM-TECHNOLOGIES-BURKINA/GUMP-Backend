package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.ApplicationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationConfigRepository extends JpaRepository<ApplicationConfig, Long> {
    @Query("SELECT a FROM ApplicationConfig a WHERE a.id = 1")
    ApplicationConfig get();
}
