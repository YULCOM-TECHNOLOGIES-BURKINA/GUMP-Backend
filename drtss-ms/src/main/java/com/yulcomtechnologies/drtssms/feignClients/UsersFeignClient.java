package com.yulcomtechnologies.drtssms.feignClients;

import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.sharedlibrary.enums.UserType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "users-ms")
public interface UsersFeignClient {
    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable("userId") String userId);

    @GetMapping("/api/users/{userId}/find")
    UserDto getUsernameOrKeycloakId(@PathVariable("userId") String userId);

    @GetMapping("/api/user/{userId}/signatory/toggle")
    UserDto toglleUserSignatoryState(@PathVariable("userId") String userId);

    @GetMapping("/api/users/{email}/email")
    UserDto findUserByEmail(@PathVariable("email") String email);

    @GetMapping("/api/users/{userType}/type")
    List<UserDto> getUserByType(
            @PathVariable UserType userType
    );
 }
