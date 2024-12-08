package com.yulcomtechnologies.usersms.repositories;

import com.yulcomtechnologies.usersms.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
