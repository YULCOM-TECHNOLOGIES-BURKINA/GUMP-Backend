package com.yulcomtechnologies.usersms.repositories;

import com.yulcomtechnologies.usersms.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findUserByEmailOrIfu(String email, String ifu);
}
