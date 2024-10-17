package com.yulcomtechnologies.gateway.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class KeycloakCreateUserDto {
    public String username;
    public String email;
    public String firstName;
    public String lastName;
    public Boolean emailVerified;
    public Boolean enabled;
    public List<Map<String, Object>> credentials = new ArrayList<>();
}
