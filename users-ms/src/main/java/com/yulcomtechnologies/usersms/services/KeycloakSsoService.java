package com.yulcomtechnologies.usersms.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.usersms.dtos.GetAdminTokenDto;
import com.yulcomtechnologies.usersms.dtos.KeycloakCreateUserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class KeycloakSsoService implements SsoProvider {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String keycloakRealmUrl;
    private final String realm;
    private final String keycloakServerUrl;

    private final String client;
    private final String clientId;

    public KeycloakSsoService(
        ObjectMapper objectMapper,
        @Value("${keycloak.auth-server-url}") String keycloakServerUrl,
        @Value("${keycloak.realm}") String realm,
        @Value("${keycloak.client}") String client,
        @Value("${keycloak.clientId}") String clientId
    ) {
        this.objectMapper = objectMapper;
        this.realm = realm;
        this.client = client;
        this.clientId = clientId;
        this.keycloakServerUrl = keycloakServerUrl;
        this.keycloakRealmUrl = keycloakServerUrl + "/realms/" + realm;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }

    public ResponseEntity<GetAdminTokenDto> getAdminToken() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var body = new LinkedMultiValueMap<String, String>();
        body.add("client_id", "admin-cli");
        body.add("username", "admin");
        body.add("password", "admin");
        body.add("grant_type", "password");

        var request = new HttpEntity<MultiValueMap<String, String>>(body, headers);
        return restTemplate.exchange(keycloakServerUrl + "/realms/master/protocol/openid-connect/token", HttpMethod.POST, request, GetAdminTokenDto.class);
    }

    public String createUser(CreateUserCommand createUserCommand) {
        var keycloakRegisterDto = new KeycloakCreateUserDto();
        var adminToken = getAdminToken().getBody();


        BeanUtils.copyProperties(createUserCommand, keycloakRegisterDto);
        keycloakRegisterDto.enabled = createUserCommand.enabled();
        keycloakRegisterDto.emailVerified = true;
        keycloakRegisterDto.credentials.add(
          Map.of(
            "type", "password",
            "value", createUserCommand.password(),
            "temporary", false
          )
        );

        keycloakRegisterDto.setAttributes(
            Map.of(
                "userType", createUserCommand.userType().name(),
                "userRole", createUserCommand.role().name()
            )
        );

        System.out.println(keycloakRegisterDto);
        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + adminToken.accessToken());
        HttpEntity<String> request = null;
        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(keycloakRegisterDto), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var url = String.format("%s/admin/realms/%s/users", keycloakServerUrl, realm);

        var response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        String locationHeader = response.getHeaders().getFirst("Location");

        if (locationHeader == null) {
            //Handle this correctly later
            return UUID.randomUUID().toString();
        }

        var userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        assignRoleToUser(getRole(createUserCommand.role().name()), userId);
        return userId;
    }

    @Override
    public void activateUser(String ssoUserId) {
        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + Objects.requireNonNull(getAdminToken().getBody()).accessToken());
        HttpEntity<String> request = new HttpEntity<>(
            "{\"enabled\": true}",
            headers
        );

        var url = String.format("%s/admin/realms/%s/users/%s", keycloakServerUrl, realm, ssoUserId);
        System.out.println(url);

        restTemplate.exchange(
            url,
            HttpMethod.PUT, request, String.class
        );
    }

    @Override
    public void toggleUserAccount(String ssoUserId, boolean statut) {
        try {
            // Récupérer le token administrateur
            String adminToken = getAdminToken().getBody().accessToken();
            if (adminToken == null || adminToken.isBlank()) {
                throw new IllegalStateException("Admin token is null or empty");
            }

            // Configurer les en-têtes
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + adminToken);

            // Construire le corps de la requête avec le statut (enabled: true/false)
            String requestBody = String.format("{\"enabled\": %b}", statut);

            // Construire l'objet HttpEntity avec le corps et les en-têtes
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Construire l'URL
            String url = String.format("%s/admin/realms/%s/users/%s", keycloakServerUrl, realm, ssoUserId);
            System.out.println("Request URL: " + url);

            // Effectuer la requête HTTP PUT
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    String.class
            );

            // Gérer la réponse
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.printf("User %s successfully toggled to %b%n", ssoUserId, statut);
            } else {
                System.err.printf("Failed to toggle user %s. HTTP Status: %s%n", ssoUserId, response.getStatusCode());
            }
        } catch (Exception e) {
            // Gestion des erreurs
            System.err.printf("Error while toggling user %s: %s%n", ssoUserId, e.getMessage());
            throw new RuntimeException("Failed to toggle user account", e);
        }
    }

    @Override
    public void deleteUser(String keycloakUserId) {
        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + Objects.requireNonNull(getAdminToken().getBody()).accessToken());
        HttpEntity<String> request = new HttpEntity<>(headers);

        var url = String.format("%s/admin/realms/%s/users/%s", keycloakServerUrl, realm, keycloakUserId);

        restTemplate.exchange(
            url,
            HttpMethod.DELETE, request, String.class
        );
    }

    public RoleInformation getRole(String role) {
        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + Objects.requireNonNull(getAdminToken().getBody()).accessToken());
        HttpEntity<String> request = new HttpEntity<>(headers);

        var url = String.format("%s/admin/realms/%s/clients/%s/roles/%s", keycloakServerUrl, realm, clientId, role);

        var result = restTemplate.exchange(
            url,
            HttpMethod.GET, request, RoleInformation.class
        );

        return result.getBody();
    }

    public void assignRoleToUser(RoleInformation roleInformation, String userId) {
        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + Objects.requireNonNull(getAdminToken().getBody()).accessToken());
        HttpEntity<String> request;


        AssignRoleToUser[] roles = {
            new AssignRoleToUser(
                roleInformation.id(),
                roleInformation.name(),
                userId,
                false,
                true
            )
        };

        try {
            request = new HttpEntity<>(objectMapper.writeValueAsString(roles), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        var url = String.format("%s/admin/realms/%s/users/%s/role-mappings/clients/%s", keycloakServerUrl, realm, userId, clientId);
        var response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        System.out.println(response.getBody());
    }

    public ResponseEntity<String> getUsers() {
        HttpEntity<String> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(keycloakRealmUrl + "/users", HttpMethod.GET, request, String.class);
    }
}

record RoleInformation (String id, String name) {}

record AssignRoleToUser(String id, String name, String containerId, boolean composite, boolean clientRole) {}
