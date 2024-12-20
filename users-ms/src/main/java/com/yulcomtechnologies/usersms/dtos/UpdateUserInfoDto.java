package com.yulcomtechnologies.usersms.dtos;

import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;
import lombok.Data;

@Data

public class UpdateUserInfoDto {
    private Long id;
    private String forename;
     private String lastname;
     private UserRole role;
    private UserType userType;
    private String region;

    private String matricule;
    private String titre_honorifique;
    private String tel;
//    private Boolean is_signatory;
 }
