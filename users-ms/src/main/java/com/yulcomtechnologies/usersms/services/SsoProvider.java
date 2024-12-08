package com.yulcomtechnologies.usersms.services;

import reactor.core.publisher.Mono;

public interface SsoProvider {
    String createUser(CreateUserCommand createUserCommand) throws Exception;

    void activateUser(String ssoUserId);

    void deleteUser(String keycloakUserId);
}
