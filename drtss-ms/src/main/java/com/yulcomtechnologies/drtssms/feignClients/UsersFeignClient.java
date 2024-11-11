package com.yulcomtechnologies.drtssms.feignClients;

import com.yulcomtechnologies.drtssms.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms")
public interface UsersFeignClient {
    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable("userId") String userId);
}
