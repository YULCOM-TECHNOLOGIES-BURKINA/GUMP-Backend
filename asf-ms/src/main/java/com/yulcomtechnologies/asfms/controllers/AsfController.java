package com.yulcomtechnologies.asfms.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.asfms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.asfms.enums.AuthRequestValue;
import com.yulcomtechnologies.asfms.services.ApiService;
import com.yulcomtechnologies.asfms.services.AsfService;
import lombok.NoArgsConstructor;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
     * DocumentRequest
     * @param id
     * @return
     */
    @GetMapping(path = "/demandes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentRequestDto getAsf(@PathVariable Long id) {
        return asfService.findById(id);
    }


    /**
     * Faire  Demande d'ASF
     * @param params
     * @return
     */
    @PostMapping(path = "/demandes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object demandeAsf(@RequestBody Map<String, String> params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.get("ifu")));
        formData.add(new BasicNameValuePair("form[nes]", params.get("nes")));
        String url = e_sintax_url+"rest/asf/demande";


        try {
            Object result =   apiService.callApi(url, formData);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writeValueAsString(result);
            JsonNode rootNode = objectMapper.readTree(jsonResult);

            String reference = rootNode.get("data").get("items").get("resultat").get("reference").asText();
            asfService.sync_request_on_local(params.get("ifu"), params.get("nes"), reference);

            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Erreur conversion en JSON\"}";
        }
    }

    /**
     *  Telecharger document asf
     * @param params
     * @return
     */
    @PostMapping(path = "/telecharger", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> telechargerAsf(@RequestBody Map<String, String> params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.get("ifu")));
        formData.add(new BasicNameValuePair("form[nes]", params.get("nes")));
        formData.add(new BasicNameValuePair("form[reference]", params.get("reference")));

        String url = e_sintax_url+"rest/asf/docs";
        byte[] pdfData = apiService.callApiForPdf(url, formData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "document.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }

    /**
     * Consultation statut asf
     * @param params
     * @return
     */
    @PostMapping(path = "/statut", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> statutAsf(@RequestBody Map<String, String> params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.get("ifu")));
        formData.add(new BasicNameValuePair("form[nes]", params.get("nes")));
        formData.add(new BasicNameValuePair("form[reference]", params.get("reference")));

        String url =  e_sintax_url+"rest/asf/statut";
        return apiService.callApi(url, formData);
    }

    /**
     * Historique demandes
     * @param params
     * @return
     */
    @GetMapping(path = "/demandes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> historiqueDemandes(@RequestBody Map<String, String> params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.get("ifu")));
        formData.add(new BasicNameValuePair("form[nes]", params.get("nes")));

        String url =  e_sintax_url+"rest/asf/historique";
        return apiService.callApi(url, formData);
    }

    /**
     * Verifier demandes asf
     * @param params
     * @return
     */
    @PostMapping(path = "/verifier", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> verifierAsf(@RequestBody Map<String, String> params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.get("ifu")));
        formData.add(new BasicNameValuePair("form[nes]", params.get("nes")));
        formData.add(new BasicNameValuePair("form[attestation]", params.get("attestation")));

        String url =  e_sintax_url+"rest/asf/verifdocs";
        return apiService.callApi(url, formData);
    }

    /**
     * Detail Asf
     * @param params
     * @return
     */
    @PostMapping(path = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> detailsAsf(@RequestBody Map<String, String> params) {
        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", params.get("ifu")));
        formData.add(new BasicNameValuePair("form[nes]", params.get("nes")));
        formData.add(new BasicNameValuePair("form[reference]", params.get("reference")));

        String url =  e_sintax_url+"rest/asf/details";
        return apiService.callApi(url, formData);
    }

}
