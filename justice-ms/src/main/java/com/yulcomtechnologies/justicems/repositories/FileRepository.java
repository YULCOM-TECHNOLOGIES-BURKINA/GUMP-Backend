package com.yulcomtechnologies.justicems.repositories;

import com.yulcomtechnologies.justicems.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
