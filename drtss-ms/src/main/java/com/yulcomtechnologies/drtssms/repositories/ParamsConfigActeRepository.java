package com.yulcomtechnologies.drtssms.repositories;

import com.yulcomtechnologies.drtssms.entities.ParamsConfigActe;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ParamsConfigActeRepository extends CrudRepository<ParamsConfigActe, Long> {
    Optional<ParamsConfigActe> findByLabelle(String labelle);
}
