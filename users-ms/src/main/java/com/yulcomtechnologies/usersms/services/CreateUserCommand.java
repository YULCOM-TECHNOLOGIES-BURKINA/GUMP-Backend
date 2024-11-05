package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.usersms.enums.UserRole;
import com.yulcomtechnologies.usersms.enums.UserType;

public record CreateUserCommand(String username, String password, String email, String firstName, String lastName, Boolean emailVerified, Boolean enabled, UserRole role, UserType userType) {
}
