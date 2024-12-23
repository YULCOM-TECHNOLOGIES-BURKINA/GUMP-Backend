package com.yulcomtechnologies.usersms.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @NotNull
    public String ifuNumber;

    @NotBlank
    @NotNull
    public String cnssNumber;

    @NotBlank
    @NotNull
    public String password;

    @NotBlank
    @NotNull
    public String passwordConfirmation;

    @NotBlank
    @NotNull
    @Email
    public String email;

    @NotNull
    @NotBlank
    public String region;

    @NotNull
    @NotBlank
    public String nes;

    @NotNull
    @NotBlank
    private String representantLastname;

    @NotNull
    @NotBlank
    private String representantFirstname;

    @NotNull
    @NotBlank
    private String representantPhone;

    @NotBlank
    @NotNull
    private String representantNip;
}
