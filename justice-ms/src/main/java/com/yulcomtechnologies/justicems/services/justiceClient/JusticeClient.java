package com.yulcomtechnologies.justicems.services.justiceClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JusticeClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String justiceApiUrl;
    private final String username;
    private final String password;

    public JusticeClient(
        @Value("${justice-service.url}") String justiceApiUrl,
        @Value("${justice-service.username}") String username,
        @Value("${justice-service.password}") String password
    ) {
        this.justiceApiUrl = justiceApiUrl;
        this.username = username;
        this.password = password;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ResponseEntity<CreateDemandeResponse> createDemandeWithFiles(
        Demande demande,
        MultipartFile extraitRccm,
        MultipartFile statutEntreprise
    ) {
        List<Document> documents = new ArrayList<>();
        demande.setUserConnected(username);

        if (extraitRccm != null) {
            documents.add(new Document("acte-derive", extraitRccm.getOriginalFilename(), "application/pdf", "pdf", "RCCM"));
        }

        if (statutEntreprise != null) {
            documents.add(new Document("acte-derive", statutEntreprise.getOriginalFilename(), "application/pdf", "pdf", "Statuts"));
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add(
                "demande", new HttpEntity<>(objectMapper.writeValueAsString(demande), createJsonHeader())
            );

            if (!documents.isEmpty()) {
                body.add(
                    "documents", new HttpEntity<>(objectMapper.writeValueAsString(documents), createJsonHeader())
                );
            }

            if (extraitRccm != null) {
                body.add("files", createFileResource(extraitRccm));
            }

            if (statutEntreprise != null) {
                body.add("files", createFileResource(statutEntreprise));
             }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            var response = authenticate().getBody();

            assert response != null;
            headers.set("Authorization", "Bearer " + response.id_token());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            System.out.println(requestEntity.getBody());

            return restTemplate.exchange(
                justiceApiUrl + "/rccm-demande/api/demande/create-with-files",
                HttpMethod.POST,
                requestEntity,
                CreateDemandeResponse.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Error creating demande with files", e);
        }
    }

    public ResponseEntity<AuthenticateResponse> authenticate() {
        var requestEntity = new HttpEntity<>(
            new AuthenticatedRequest(username, password)
        );

        return restTemplate.exchange(
            justiceApiUrl + "/administration-service/api/authenticate",
            HttpMethod.POST,
            requestEntity,
            AuthenticateResponse.class
        );
    }

    private Resource createFileResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

    private HttpHeaders createJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public VerifyDocumentResponseDto verifyDocument(String documentNumber) {
        var authenticateResponse = authenticate().getBody();
        var headers = new HttpHeaders();
        assert authenticateResponse != null;
        headers.set("Authorization", "Bearer " + authenticateResponse.id_token());

        var response = restTemplate.exchange(
            justiceApiUrl + "/rccm-demande/api/demande/validite-acte/" + documentNumber,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            VerifyDocumentResponse.class
        ).getBody();

        assert response != null;

        log.info("Response: {}", response);


        return new VerifyDocumentResponseDto(
            documentNumber,
            "",
            "",
            "",
            !response.isExpired()
        );
    }
}
