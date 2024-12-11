package com.yulcomtechnologies.usersms.dtos;

import lombok.Data;

import java.time.LocalDateTime;

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
    private Boolean isActive;
    private CompanyDto company;

    private String matricule;
    private String titre_honorifique;
    private String tel;
    private LocalDateTime createdAt;

}
