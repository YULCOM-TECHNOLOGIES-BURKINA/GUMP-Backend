package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.dtos.CreateUserRequest;
import com.yulcomtechnologies.usersms.dtos.UserDto;
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

@RestController("users")
@AllArgsConstructor
public class UsersController {
    private final CorporationInfosExtractor corporationInfosExtractor;
    private final UserService userService;
    private final AuthService authService;

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

    @GetMapping("users/{id}")
    public ResponseEntity<UserDto> getUser(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getUser(id));
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

    @GetMapping("users/{id}/toggle")
    public ResponseEntity<Void> toglleUserAccountState(
            @PathVariable Long id
    ) {
        authService.toglleUserAccountState(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("users/{id}/reject")
    public ResponseEntity<Void> rejectPendingAccount(
        @PathVariable Long id
    ) {
        authService.rejectAccountCreation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("users/{id}/status/toggle")
    public ResponseEntity<Void> toggleUserStatus(
            @PathVariable Long id
    ) {
        authService.validatePendingUserAccount(id);
        return ResponseEntity.ok().build();
    }


}
