package com.yulcomtechnologies.gateway.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.gateway.dtos.GetAdminTokenDto;
import com.yulcomtechnologies.gateway.dtos.KeycloakCreateUserDto;
import com.yulcomtechnologies.gateway.dtos.RegisterRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KeycloakAdminService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String keycloakRealmUrl;
    private final String realm;

    private final String keycloakServerUrl;

    public KeycloakAdminService(
        ObjectMapper objectMapper,
        @Value("${keycloak.auth-server-url}") String keycloakServerUrl,
        @Value("${keycloak.realm}") String realm
    ) {
        this.objectMapper = objectMapper;
        this.realm = realm;
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

    public ResponseEntity<String> createUser(RegisterRequest registerRequest) throws JsonProcessingException {
        var keycloakRegisterDto = new KeycloakCreateUserDto();
        var adminToken = getAdminToken().getBody();


        BeanUtils.copyProperties(registerRequest, keycloakRegisterDto);
        keycloakRegisterDto.enabled = true;
        keycloakRegisterDto.emailVerified = true;
        keycloakRegisterDto.credentials.add(
          Map.of(
            "type", "password",
            "value", registerRequest.password,
            "temporary", false
          )
        );

        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + adminToken.accessToken());
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(keycloakRegisterDto), headers);
        var url = String.format("%s/admin/realms/%s/users", keycloakServerUrl, realm);

        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    public ResponseEntity<String> getUsers() {
        HttpEntity<String> request = new HttpEntity<>(getHeaders());
        return restTemplate.exchange(keycloakRealmUrl + "/users", HttpMethod.GET, request, String.class);
    }

}
