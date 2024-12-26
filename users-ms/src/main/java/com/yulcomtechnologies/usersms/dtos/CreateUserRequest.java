package com.yulcomtechnologies.usersms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private String region;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String forename;

    @NotNull
    @NotBlank
    private String password;

    @NotNull
    @NotBlank
    private String lastname;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String role;

    @NotNull
    @NotBlank
    private String userType;

    private String matricule;

    private String titre_honorifique;

    private String tel;
}
