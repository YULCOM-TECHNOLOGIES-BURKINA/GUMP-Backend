package com.yulcomtechnologies.justicems.services.justiceClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.Demande;
import com.yulcomtechnologies.justicems.services.justiceClient.dtos.Document;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JusticeClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String JUSTICE_API_URL = "http://10.52.134.14:8443/";

    public JusticeClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }


    public ResponseEntity<String> createDemandeWithFiles(
        Demande demande,
        MultipartFile extraitRccm,
        MultipartFile statutEntreprise
    ) {
        List<Document> documents = new ArrayList<>();

        if (extraitRccm != null) {
            documents.add(new Document("Recepissé RCCM", "Recepissé RCCM", "application/pdf", "pdf", "RCCM"));
        }

        if (statutEntreprise != null) {
            documents.add(new Document("Statut Entreprise", "Statut Entreprise", "application/pdf", "pdf", "Statuts"));
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("demande", objectMapper.writeValueAsString(demande));

            if (!documents.isEmpty()) {
                body.add("documents", objectMapper.writeValueAsString(documents));
            }

            if (extraitRccm != null) {
                body.add("files", createFileResource(extraitRccm));
            }

            if (statutEntreprise != null) {
                body.add("files", createFileResource(statutEntreprise));
             }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            //headers.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmZXJkaW5hbmQ3N2tvdXJhQGdtYWlsLmNvbSIsImF1dGgiOiJST0xFX1VTRVIiLCJzdHJ1Y3R1cmUiOiJUQy1PVUFHQSIsInR5cGVTdHJ1Y3R1cmUiOiJUUklCVU5BTCIsImV4cCI6MTczMjIwODg4M30.ikf8dt4OHcIqI6NGMH4xXlIkWKdQBCgfo-W-kcMZCZU8f4_QOJHPPRItPE1EE-mnYsUJUZfzjjeX1xqp1d0m5Q");
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            return restTemplate.exchange(
                JUSTICE_API_URL + "rccm-demande/api/demande/create-with-files",
                HttpMethod.POST,
                requestEntity,
                String.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Error creating demande with files", e);
        }
    }

    private Resource createFileResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }
}
