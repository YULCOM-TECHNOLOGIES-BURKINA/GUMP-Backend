package com.yulcomtechnologies.usersms.services;

public interface SsoProvider {
    String createUser(CreateUserCommand createUserCommand) throws Exception;
}
