package com.yulcomtechnologies.usersms.services;

public interface SsoProvider {
    String createUser(CreateUserCommand createUserCommand) throws Exception;

    void activateUser(String ssoUserId);

    void toggleUserAccount(String ssoUserId, boolean statut) ;

    void deleteUser(String keycloakUserId);
}
