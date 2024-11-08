package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.dtos.CreateUserRequest;
import com.yulcomtechnologies.usersms.dtos.UserDto;
import com.yulcomtechnologies.usersms.enums.UserType;
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
        @RequestParam(required = false) UserType userType
    ) {
        return ResponseEntity.ok(userService.getUsers(pageable, userType));
    }
}
