package com.yulcomtechnologies.asfms.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
@Transactional
public class ApiService {

    @Autowired
    private AuthService authService;

    /**
     *
     * @return
     * @throws Exception
     */
    private CloseableHttpClient createHttpClientWithDisabledSSL() throws Exception {
        // Configurer SSL pour accepter tous les certificats
        TrustStrategy acceptingTrustStrategy = (certChain, authType) -> true;
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(
                        SSLConnectionSocketFactoryBuilder.create()
                                .setSslContext(sslContext)
                                .build()
                )
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .build();
    }

    /**
     *
     * @param url
     * @param formData
     * @return
     */
    public Map<String, Object> callApi(String url, List<BasicNameValuePair> formData) {
        Map<String, Object> responseMap = new HashMap<>();
        try (CloseableHttpClient httpClient = createHttpClientWithDisabledSSL()) {

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(formData));
            httpPost.setHeader("Authorization", "Bearer " + authService.authenticate()); // Ajouter le Bearer Token

             try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                responseMap.put("status", statusCode);

                 BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                StringBuilder responseContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }

                 ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(responseContent.toString());

                 responseMap.put("data", responseJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("error", e.getMessage());
        }
        return responseMap;
    }


    /**
     *
     * @param url
     * @param formData
     * @return
     */
    public byte[] callApiForPdf(String url, List<BasicNameValuePair> formData) {
        try (CloseableHttpClient httpClient = createHttpClientWithDisabledSSL()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Authorization", "Bearer " + authService.authenticate());
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new UrlEncodedFormEntity(formData));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                InputStream inputStream = response.getEntity().getContent();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                return byteArrayOutputStream.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
