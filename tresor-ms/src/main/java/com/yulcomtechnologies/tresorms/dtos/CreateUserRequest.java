package com.yulcomtechnologies.tresorms.dtos;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String forename;
    private String password;
    private String lastname;
    private String email;
}
