package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.enums.UserRole;
import com.yulcomtechnologies.sharedlibrary.enums.UserType;
import com.yulcomtechnologies.tresorms.dtos.CreateUserRequest;
import com.yulcomtechnologies.tresorms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.tresorms.feignClients.dtos.UsersMsCreateUserRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UsersFeignClient usersFeignClient;

    public void createUser(CreateUserRequest createUserRequest) {
        var usersMsCreateUserRequest = new UsersMsCreateUserRequest();
        BeanUtils.copyProperties(createUserRequest, usersMsCreateUserRequest);
        usersMsCreateUserRequest.setUserType(UserType.TRESOR_USER.name());
        usersMsCreateUserRequest.setRole(UserRole.TRESOR_AGENT.name());

        usersFeignClient.createUser(usersMsCreateUserRequest);
    }

    public Page getUsers() {
        return usersFeignClient.getUsers();
    }
}
