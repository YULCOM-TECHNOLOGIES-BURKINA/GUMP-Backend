package com.yulcomtechnologies.usersms.services;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScrapingCorporationInfosExtractor implements CorporationInfosExtractor {
    private static final String IFU_VERIFICATION_URL = "https://dgi.bf/verification/verification-ifu";

    @Override
    public Optional<CorporationData> extractCorporationInfos(String ifuNumber) throws Exception {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("IFU", ifuNumber);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(IFU_VERIFICATION_URL, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            var data = parseHtmlResponse(response.getBody());

            if (!data.isEmpty()) {
                return Optional.of(
                    new CorporationData(
                        data.get("Enseigne Commercial"),
                        data.get("Adresse"),
                        data.get("Téléphone") != null ? data.get("Téléphone").replace("-", "") : "",
                        data.get("Mail"),
                        data.get("website"),
                        data.get("N° RCCM")
                    )
                );
            }
        } else {
            throw new Exception("Failed to retrieve IFU information");
        }

        return Optional.empty();
    }

    private Map<String, String> parseHtmlResponse(String html) {
        Document doc = Jsoup.parse(html);
        Map<String, String> result = new HashMap<>();

        for (Element row : doc.select("table.table tr")) {
            String key = Objects.requireNonNull(row.select("td").first()).text();
            String value = Objects.requireNonNull(row.select("td").last()).text();
            result.put(key, value);
        }

        return result;
    }
}
