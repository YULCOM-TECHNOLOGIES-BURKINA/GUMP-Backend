package com.yulcomtechnologies.feignClients;

import com.yulcomtechnologies.feignClients.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms")
public interface UserMsFeignClient {
    @GetMapping("/api/users/{userId}/find")
    UserDto getUser(@PathVariable("userId") String userId);
}
