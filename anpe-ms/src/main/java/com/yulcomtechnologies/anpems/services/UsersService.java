package com.yulcomtechnologies.anpems.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.anpems.dtos.UsersDto;
import com.yulcomtechnologies.anpems.enums.AuthRequestValue;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class UsersService {



    @Autowired
    private ApiService apiService;

    @Autowired
    private AuthService authService;

    /**
     *
     * @return
     */
    public Map<String, Object> getUsersAuthenticateInfo() {
        Map<String, Object> responseMap = new HashMap<>();

        try (CloseableHttpClient httpClient = apiService.createHttpClientWithDisabledSSL()) {
            // Préparer la requête
            HttpGet httpGet = new HttpGet(AuthRequestValue.ANPE_URL.getValue());
            httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpGet.setHeader("Authorization", "Bearer " + authService.authenticate());

            // Exécuter la requête
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getCode();
                responseMap.put("status", statusCode);

                // Lire le contenu de la réponse
                String responseContent = EntityUtils.toString(response.getEntity());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(responseContent);

                responseMap.put("data", responseJson);
            }
        } catch (IOException e) {
            // Gestion des erreurs liées aux I/O
            responseMap.put("error", "Erreur d'entrée/sortie : " + e.getMessage());
        } catch (Exception e) {
            // Gestion d'autres erreurs
            responseMap.put("error", "Une erreur inattendue s'est produite : " + e.getMessage());
        }

        return responseMap;
    }

}
