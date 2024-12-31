package com.yulcomtechnologies.tresorms.repositories;

 import com.yulcomtechnologies.tresorms.entities.ParamsConfigActe;
 import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ParamsConfigActeRepository extends CrudRepository<ParamsConfigActe, Long> {
    Optional<ParamsConfigActe> findByLabelle(String labelle);
}
