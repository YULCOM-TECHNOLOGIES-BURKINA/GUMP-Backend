package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.usersms.dtos.CreateUserRequest;
import com.yulcomtechnologies.usersms.dtos.UserDto;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.mappers.UserMapper;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {
    private final SsoProvider ssoProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


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
            .forename(createUserRequest.getForename())
            .lastname(createUserRequest.getLastname())
            .region(createUserRequest.getRegion())
            .username(createUserRequest.getUsername())
            .role(UserRole.valueOf(createUserRequest.getRole()))
            .userType(UserType.valueOf(createUserRequest.getUserType()))
            .build();

        userRepository.save(user);
    }

    public Page<UserDto> getUsers(Pageable pageable, UserType userType) {
        var users =
            userType == null ? userRepository.findAll(pageable): userRepository.findAllByUserType(pageable, userType);
        return users.map(userMapper::toUserDto);
    }

    public UserDto getUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(
            () -> new RuntimeException("User not found")
        );

        return userMapper.toUserDto(user);
    }
}
