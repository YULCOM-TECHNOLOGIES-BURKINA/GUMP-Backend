package com.yulcomtechnologies.usersms.controllers;

import com.yulcomtechnologies.usersms.services.CorporationData;
import com.yulcomtechnologies.usersms.services.CorporationInfosExtractor;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("users")
@AllArgsConstructor
public class UsersController {
    private final CorporationInfosExtractor corporationInfosExtractor;

    @GetMapping("get-ifu/{ifu}")
    public ResponseEntity<CorporationData> getUsers(
        @PathVariable String ifu
    ) throws Exception {
        var data = corporationInfosExtractor.extractCorporationInfos(ifu).orElseThrow(
            () -> new RuntimeException("Corporation not found")
        );

        return ResponseEntity.ok(data);
    }

    @PostMapping("auth/register")
    public ResponseEntity<?> createUser() throws Exception {
        return ResponseEntity.ok().build();
    }
}
