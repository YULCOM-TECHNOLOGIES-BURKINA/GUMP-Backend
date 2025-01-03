package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.dtos.CreateUserRequest;
import com.yulcomtechnologies.usersms.dtos.UpdateProfileRequest;
import com.yulcomtechnologies.usersms.dtos.UpdateUserInfoDto;
import com.yulcomtechnologies.usersms.dtos.UserDto;
import com.yulcomtechnologies.usersms.entities.User;
import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import com.yulcomtechnologies.usersms.services.AuthService;
import com.yulcomtechnologies.usersms.services.CorporationData;
import com.yulcomtechnologies.usersms.services.CorporationInfosExtractor;
import com.yulcomtechnologies.usersms.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("users")
@AllArgsConstructor
public class UsersController {
    private final CorporationInfosExtractor corporationInfosExtractor;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
        @PathVariable Long id
    ) {
        userService.toggleUserAccount(id, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("users/{id}/activate")
    public ResponseEntity<Void> activateUser(
        @PathVariable Long id
    ) {
        userService.toggleUserAccount(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("get-ifu/{ifu}")
    public ResponseEntity<CorporationData> getUsers(
        @PathVariable String ifu
    ) throws Exception {
        var data = corporationInfosExtractor.extractCorporationInfos(ifu).orElseThrow(
            () -> new RuntimeException("Corporation not found")
        );

        return ResponseEntity.ok(data);
    }

    @PostMapping("users")
    public ResponseEntity<?> createUser(
        @Validated @RequestBody CreateUserRequest createUserRequest
    ) throws Exception {
        userService.createUser(createUserRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("users")
    public ResponseEntity<Page<UserDto>> getUsers(
        Pageable pageable,
        @RequestParam(required = false) Boolean isActivated,
        @RequestParam(required = false) UserType userType
    ) {
        return ResponseEntity.ok(userService.getUsers(pageable, userType));
    }

    @GetMapping("inactive-users")
    public ResponseEntity<Page<UserDto>> getInactiveUsers(
        Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getInactiveUsers(pageable));
    }

    @GetMapping("pending-for-activation-users")
    public ResponseEntity<Page<UserDto>> getPendingForActivationUsers(
        Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getInactiveUsers(pageable));
    }

    @GetMapping("users/{id}")
    public ResponseEntity<UserDto> getUser(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getUser(id));
    }
    @GetMapping("users/{email}/email")
    public ResponseEntity<User> findUserByEmail(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("users/{usernameOrSsoId}/find")
    public ResponseEntity<UserDto> findUser(
        @PathVariable String usernameOrSsoId
    ) {
        return ResponseEntity.ok(userService.findUser(usernameOrSsoId));
    }

    @PostMapping("users/{id}/approve")
    public ResponseEntity<Void> approvePendingAccount(
        @PathVariable Long id
    ) {
        authService.validatePendingUserAccount(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("users/{id}/reject")
    public ResponseEntity<Void> rejectPendingAccount(
        @PathVariable Long id
    ) {
        authService.rejectAccountCreation(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Activer/Desactiver compte
     * @param id
     * @return
     */
    @GetMapping("user/{id}/toggle")
    public ResponseEntity<Void> toglleUserAccountState(
            @PathVariable Long id
    ) {
        authService.toglleUserAccountState(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Activer/Desactiver compte du signataire
     * @param id
     * @return
     */
    @GetMapping("user/{id}/signatory/toggle")
    public ResponseEntity<Void> toglleUserSignatoryState(
            @PathVariable Long id
    ) {
        userService.toglleUserSignatoryState(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Liste Users Par type
     * @param userType
     * @return
     */
    @GetMapping("users/{userType}/type")
    public List<User> getUserByType(
            @PathVariable UserType userType
    ) {
        return  userService.getUserByType(userType);

    }

    /**
     *
     * @param userType
     * @param region
     * @return
     */
    @GetMapping("users/type/{userType}/region/{region}")
    public List<User> getUserByTypeAndRegion(
            @PathVariable UserType userType,
            @PathVariable String region
    ) {
        return  userService.getUserByTypeAndRegion(userType,region);

    }

    @GetMapping("users/me")
    public ResponseEntity<UserDto> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    @PostMapping("users/me")
    public ResponseEntity<Void> updateProfile(
        @Validated @RequestBody UpdateProfileRequest updateProfileRequest
    ) {
        userService.updateProfile(updateProfileRequest);
        return ResponseEntity.ok().build();
    }


    /**
     * Mettre a jour les informations d'un utilisateur
     * @param updateUserInfo
     * @return
     */
    @PostMapping("users/update")
    public ResponseEntity<Void> updateUserInfo(
        @Validated @RequestBody UpdateUserInfoDto updateUserInfo
    ) {
        userService.updateUserInfo(updateUserInfo);
        return ResponseEntity.ok().build();
    }

    /**
     * Vérifie si un utilisateur avec un rôle donné existe dans une région donnée.
     *
     * @param role   Le rôle de l'utilisateur
     * @param region La région de l'utilisateur
     * @return true si l'utilisateur existe, false sinon
     */
    @GetMapping("users/exists")
    public ResponseEntity<Boolean> checkUserExists(
            @RequestParam("role") UserRole role,
            @RequestParam("region") String region) {
        boolean exists = userService.isUserWithRoleInRegion(role, region);
        return ResponseEntity.ok(exists);
    }
}
