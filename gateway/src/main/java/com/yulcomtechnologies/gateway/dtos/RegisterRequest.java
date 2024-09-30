package com.yulcomtechnologies.gateway.dtos;

import lombok.Data;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public String firstName;
    public String lastName;
}
