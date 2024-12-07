package com.yulcomtechnologies.asfms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AuthRequestDto {
    private String email;
    private String password;
}
