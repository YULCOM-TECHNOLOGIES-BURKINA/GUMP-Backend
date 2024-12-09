package com.yulcomtechnologies.anpems.controllers;

import com.yulcomtechnologies.anpems.services.ApiService;
import com.yulcomtechnologies.anpems.services.AuthService;
import com.yulcomtechnologies.anpems.services.UsersService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class UsersControllers {


    private final ApiService apiService;

    private final UsersService usersService;


    @GetMapping("/authenticated-info")
    public ResponseEntity<Map<String, Object>> getAuthenticatedUserInfo() {
        Map<String, Object> response = usersService.getUsersAuthenticateInfo();

        if (response.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
