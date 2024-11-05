package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.dtos.RegisterRequest;
import com.yulcomtechnologies.usersms.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("auth/register")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest) throws Exception {
        authService.register(registerRequest);

        return ResponseEntity.ok().build();
    }

    /*@GetMapping("test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test() {
        return "TEST";
    }*/
}
