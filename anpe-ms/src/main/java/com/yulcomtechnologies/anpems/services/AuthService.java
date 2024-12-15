package com.yulcomtechnologies.anpems.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.anpems.enums.AuthRequestValue;
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
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private String token;

    /**
     * authenticate user
     * @return
     */
    public String authenticate() {
         try {
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

            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .evictExpiredConnections()
                    .evictIdleConnections(TimeValue.ofSeconds(30))
                    .build()) {

                 HttpPost httpPost = new HttpPost(AuthRequestValue.ANPE_URL.getValue()+"/login");
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");


                List<BasicNameValuePair> form = new ArrayList<>();
                form.add(new BasicNameValuePair("email", AuthRequestValue.ANPE_AUTH_EMAIL.getValue()));
                form.add(new BasicNameValuePair("password", AuthRequestValue.ANPE_AUTH_PASSWORD.getValue()));
                httpPost.setEntity(new UrlEncodedFormEntity(form));

                 try (CloseableHttpResponse response = httpClient.execute(httpPost)) {

                     BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder responseContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseContent.append(line);
                    }

                     ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode responseJson = objectMapper.readTree(responseContent.toString());

                     if (responseJson.has("token") && responseJson.get("token").has("jwt")) {
                        String jwtToken = responseJson.get("token").get("jwt").asText();
                        token = jwtToken;
                        return token;

                     } else {
                         return null;
                     }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
         }
        return token;
    }


    public String getToken() {
        return token;
    }
}

