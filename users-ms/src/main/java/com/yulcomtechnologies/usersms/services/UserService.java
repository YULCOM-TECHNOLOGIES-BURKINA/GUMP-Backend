package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.usersms.dtos.CreateUserRequest;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final SsoProvider ssoProvider;
    private final UserRepository userRepository;

    @Transactional
    public void createUser(CreateUserRequest createUserRequest) throws Exception {
        var ssoUserId = ssoProvider.createUser(
            new CreateUserCommand(
                createUserRequest.getUsername(),
                createUserRequest.getPassword(),
                createUserRequest.getEmail(),
                createUserRequest.getUsername(),
                createUserRequest.getUsername(),
                true,
                true,
                UserRole.valueOf(createUserRequest.getRole()),
                UserType.valueOf(createUserRequest.getUserType())
            )
        );

        var user = User.builder()
            .email(createUserRequest.getEmail())
            .keycloakUserId(ssoUserId)
            .role(UserRole.valueOf(createUserRequest.getRole()))
            .userType(UserType.valueOf(createUserRequest.getUserType()))
            .build();

        userRepository.save(user);
    }
}
