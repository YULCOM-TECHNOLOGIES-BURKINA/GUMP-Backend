package com.yulcomtechnologies.usersms.services;

import com.yulcomtechnologies.usersms.BaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class KeycloakServiceTest extends BaseIntegrationTest {
    @Autowired
    KeycloakSsoService keycloakService;

    @Test
    void activateUser() {
        keycloakService.activateUser("b29a6fc2-5034-4c58-bbe5-c79a5080ffd9");
    }

    @Test
    void deleteUser() {
        keycloakService.deleteUser("47524d0a-51b6-4485-b5e0-be4ab6741fc0");
    }
}
