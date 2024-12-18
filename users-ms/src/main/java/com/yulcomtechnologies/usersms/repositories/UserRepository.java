package com.yulcomtechnologies.usersms.repositories;

import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findUserByEmailOrIfu(String email, String ifu);
    Page<User> findAllByUserType(Pageable pageable, UserType userType);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Page<User> findAllByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = :usernameOrSsoUserId OR u.keycloakUserId = :usernameOrSsoUserId")
    Optional<User> findByUsernameOrKeycloakUserId(String usernameOrSsoUserId);

    Page<User> findAllByIsActiveFalse(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.userType = :userType")
    List<User> findByuserType(UserType userType);

    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.region = :region")
    List<User> finUserByTypeAndRegion(UserType userType,String region);

}
