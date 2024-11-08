package com.yulcomtechnologies.usersms.repositories;

import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findUserByEmailOrIfu(String email, String ifu);
    Page<User> findAllByUserType(Pageable pageable, UserType userType);
}
