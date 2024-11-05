package com.yulcomtechnologies.usersms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    public String ifuNumber;
    public String password;
    public String passwordConfirmation;
    public String email;
}
