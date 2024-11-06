package com.yulcomtechnologies.tresorms.feignClients.dtos;

import lombok.Data;

@Data
public class UsersMsCreateUserRequest {
    private String username;
    private String forename;
    private String password;
    private String lastname;
    private String email;
    private String role;
    private String userType;
}
