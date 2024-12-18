package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.usersms.dtos.CreateUserRequest;
import com.yulcomtechnologies.usersms.dtos.UpdateProfileRequest;
import com.yulcomtechnologies.usersms.dtos.UserDto;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.events.AccountStateChanged;
import com.yulcomtechnologies.usersms.mappers.UserMapper;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class UserService {
    private final SsoProvider ssoProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;
    private final EventPublisher eventPublisher;
    private final AuthenticatedUserService authenticatedUserService;

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
            .isActive(true)
            .forename(createUserRequest.getForename())
            .lastname(createUserRequest.getLastname())
            .region(createUserRequest.getRegion())
            .username(createUserRequest.getUsername())
            .role(UserRole.valueOf(createUserRequest.getRole()))
            .userType(UserType.valueOf(createUserRequest.getUserType()))

            .matricule(createUserRequest.getMatricule())
            .titre_honorifique(createUserRequest.getTitre_honorifique())
            .tel(createUserRequest.getTel())
            .userType(UserType.valueOf(createUserRequest.getUserType()))
            .is_signatory(false)
            .createdAt(LocalDateTime.now())
            .build();

        userRepository.save(user);
    }

    public Page<UserDto> getUsers(Pageable pageable, UserType userType) {
        var users =
            userType == null ? userRepository.findAll(pageable): userRepository.findAllByUserType(pageable, userType);
        return users.map(this::mapUser);
    }

    public UserDto getUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(
            () -> new RuntimeException("User not found")
        );

        return mapUser(user);
    }

    public UserDto findUser(String usernameOrSsoId) {
        var user = userRepository.findByUsernameOrKeycloakUserId(usernameOrSsoId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        return mapUser(user);
    }

    public Page<UserDto> getInactiveUsers(Pageable pageable) {
        var users = userRepository.findAllByIsActiveFalse(pageable);
        return users.map(this::mapUser);
    }

    private UserDto mapUser(User user) {
        var userDto = userMapper.toUserDto(user);
        var company = user.getCompany();

        if (company != null) {
            if (company.getEnterpriseStatut() != null) {
                userDto.getCompany().setStatutDocumentPath(
                    fileStorageService.getPath(company.getEnterpriseStatut())
                );
            }

            if (company.getIdDocument() != null) {
                userDto.getCompany().setCnibDocumentPath(fileStorageService.getPath(company.getIdDocument()));
            }
        }

        return userDto;
    }

    @Transactional
    public void toglleUserSignatoryState(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );

        user.setIs_signatory(!user.getIs_signatory());
        userRepository.save(user);
        eventPublisher.dispatch(new AccountStateChanged(user.getId()));
    }

    public User getUserByEmail(String email) {

        return (userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        ));
    }

    public List<User> getUserByType(UserType userType ) {
       return userRepository.findByuserType(userType);
    }

    public UserDto getMe() {
        var authenticatedUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(
            () -> new BadRequestException("User not found")
        );

        var userData = userRepository.findByUsernameOrKeycloakUserId(authenticatedUser.getKeycloakUserId()).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        return mapUser(userData);
    }

    public void updateProfile(UpdateProfileRequest updateProfileRequest) {
        var authenticatedUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(
            () -> new BadRequestException("User not found")
        );

        var user = userRepository.findByUsernameOrKeycloakUserId(authenticatedUser.getKeycloakUserId()).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        if (updateProfileRequest.getRegion() != null) {
            user.setRegion(updateProfileRequest.getRegion());
        }

        var company = user.getCompany();
        company.setAddress(updateProfileRequest.getAddress());
        company.setLocation(updateProfileRequest.getLocation());
        company.setPostalAddress(updateProfileRequest.getPostalAddress());
        company.setPhone(updateProfileRequest.getPhone());
        company.setRepresentantLastname(updateProfileRequest.getRepresentantLastname());
        company.setRepresentantFirstname(updateProfileRequest.getRepresentantFirstname());
        company.setRepresentantPhone(updateProfileRequest.getRepresentantPhone());
        userRepository.save(user);
    }
}
