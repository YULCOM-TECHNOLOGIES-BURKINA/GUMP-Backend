package com.yulcomtechnologies.usersms.dtos;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String forename;
    private String cnssNumber;
    private String lastname;
    private String email;
    private String role;
    private String userType;
    private String region;
    private String username;
    private CompanyDto company;
}
