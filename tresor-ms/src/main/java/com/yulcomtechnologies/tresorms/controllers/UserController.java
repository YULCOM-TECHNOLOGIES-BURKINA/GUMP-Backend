package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.dtos.CreateUserRequest;
import com.yulcomtechnologies.tresorms.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody @Validated CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);

        return ResponseEntity.ok().build();
    }
}
