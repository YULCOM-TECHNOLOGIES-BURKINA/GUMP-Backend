package com.yulcomtechnologies.tresorms.feignClients;

import com.yulcomtechnologies.tresorms.dtos.UserDto;
import com.yulcomtechnologies.tresorms.feignClients.dtos.UsersMsCreateUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "users-ms")
public interface UsersFeignClient {
    @PostMapping("/api/users")
    void createUser(UsersMsCreateUserRequest request);

    @GetMapping("/api/users/{userId}/find")
    UserDto getUsernameOrKeycloakId(@PathVariable("userId") String userId);
}
