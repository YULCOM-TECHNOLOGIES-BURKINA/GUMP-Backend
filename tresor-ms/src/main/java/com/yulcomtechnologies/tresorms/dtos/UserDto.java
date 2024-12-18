package com.yulcomtechnologies.tresorms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String forename;
    private String lastname;
    private String email;
    private String role;
    private String userType;
    private String region;
    private String username;
    private CompanyDto company;

    private String matricule;
    private String titre_honorifique;
    private String tel;
    private String keycloakUserId;

}
