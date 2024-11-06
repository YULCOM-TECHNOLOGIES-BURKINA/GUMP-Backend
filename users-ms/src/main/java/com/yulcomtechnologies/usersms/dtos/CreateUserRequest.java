package com.yulcomtechnologies.usersms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String username;
    private String forename;
    private String password;
    private String lastname;
    private String email;
    private String role;
    private String userType;
}
