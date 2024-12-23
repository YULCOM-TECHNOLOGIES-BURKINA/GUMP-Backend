package com.yulcomtechnologies.asfms.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.asfms.dtos.*;
import com.yulcomtechnologies.asfms.enums.AuthRequestValue;
import com.yulcomtechnologies.asfms.services.ApiService;
import com.yulcomtechnologies.asfms.services.AsfService;
import lombok.NoArgsConstructor;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@NoArgsConstructor
 public class AsfController {
    @Autowired
    private AsfService asfService;

    @Autowired
    private ApiService apiService;

    private  String e_sintax_url= AuthRequestValue.E_SINTAXE.getValue();



    /**
     * Faire  Demande d'ASF
     * @param params
     * @return
     */
    @PostMapping(path = "/demandes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> demandeAsf(@RequestBody AsfDmResquestDto params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.getIfu()));
        formData.add(new BasicNameValuePair("form[nes]", params.getNes()));
        String url = e_sintax_url + "rest/asf/demande";

        try {
            Object result = apiService.callApi(url, formData);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writeValueAsString(result);
            JsonNode rootNode = objectMapper.readTree(jsonResult);

            if (rootNode.has("data") && rootNode.get("data").has("items")
                    && rootNode.get("data").get("items").has("resultat")
                    && rootNode.get("data").get("items").get("resultat").has("reference")) {

                String reference = rootNode.get("data").get("items").get("resultat").get("reference").asText();
                asfService.sync_request_on_local(params.getIfu(), params.getNes(), reference);

                return ResponseEntity.ok(jsonResult);
            } else {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{ \"error\": {\n" +
                                "        \"code\": 404,\n" +
                                "        \"message\": \"Contribuable non adhérant ou IFU désactivé.\",\n" +
                                "        \"message_code\": \"Erreur NES ou ifu désactivé\"\n" +
                                "    }" +
                                "}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Une erreur inconnue s'est produite.";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("{\"error\": \"Erreur interne : %s\"}", errorMessage));
        }
    }

    /**
     * Consultation statut asf
     * @param params
     * @return
     */
    @PostMapping(path = "/statut", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> statutAsf(@RequestBody AsfResquestDto params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.getIfu()));
        formData.add(new BasicNameValuePair("form[nes]", params.getNes()));
        formData.add(new BasicNameValuePair("form[reference]", params.getReference()));

        String url =  e_sintax_url+"rest/asf/statut";
        return apiService.callApi(url, formData);
    }

    /**
     * Historique demandes
     * @param params
     * @return
     */
    @PostMapping(path = "/historique", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> historiqueDemandes(@RequestBody AsfDmResquestDto params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.getIfu()));
        formData.add(new BasicNameValuePair("form[nes]", params.getNes()));

        String url =  e_sintax_url+"rest/asf/historique";
        return apiService.callApi(url, formData);
    }


    /**
     * verify_esyntax
     * @param params
     * @return
     */
    @PostMapping(path = "/verify_esyntax", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> verify_esyntax(@RequestBody AsfDmResquestDto params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.getIfu()));
        formData.add(new BasicNameValuePair("form[nes]", params.getNes()));

        String url =  e_sintax_url+"rest/asf/contribuable";
        return apiService.callApi(url, formData);
    }



    /**
     * Detail Asf
     * @param params
     * @return
     */
    @PostMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> detailsAsf(@RequestBody AsfResquestDto params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.getIfu()));
        formData.add(new BasicNameValuePair("form[nes]", params.getNes()));
        formData.add(new BasicNameValuePair("form[reference]", params.getReference()));

        String url =  e_sintax_url+"rest/asf/details";
        return apiService.callApi(url, formData);
    }

    /**
     * verifdocs  Asf
     * @param params
     * @return
     */
    @PostMapping(path = "/verify_asf_doc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifierAsfDoc(@RequestBody AsfResquestDto params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.getIfu()));
        formData.add(new BasicNameValuePair("form[nes]", params.getNes()));
        formData.add(new BasicNameValuePair("form[reference]", params.getReference()));

        String url = e_sintax_url + "rest/asf/docs";

        byte[] pdfData = apiService.callApiForPdf(url, formData);

        if (pdfData == null || pdfData.length < 500) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Le document PDF est introuvable."));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "document.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }


}
