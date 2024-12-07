package com.yulcomtechnologies.asfms.services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yulcomtechnologies.asfms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.asfms.entities.DocumentRequest;
import com.yulcomtechnologies.asfms.enums.AuthRequestValue;
import com.yulcomtechnologies.asfms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.asfms.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.asfms.repositories.DocumentRequestRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class AsfService {

    @Autowired
    private DocumentRequestRepository documentRequestRepository;

    @Autowired
    private DocumentRequestMapper documentRequestMapper;

    @Autowired
    private ApiService e_sintax_service;


    /**
     *
     * @param documentRequestDto
     * @return
     */
    public DocumentRequest saveAsf(DocumentRequestDto documentRequestDto) {
         DocumentRequest documentRequest= documentRequestMapper.documentRequestToEntity(documentRequestDto);
         DocumentRequest savedRequest = documentRequestRepository.save(documentRequest);
         return savedRequest;
    }


    /**
     *
     * @param id
     * @return
     */
     public DocumentRequestDto findById(Long id) {
        DocumentRequest documentRequest = documentRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        return documentRequestMapper.documentEntityToDto(documentRequest);
    }


    /**
     *
     * @param ifu
     * @param nes
     * @param reference
     * @return
     * @throws JsonProcessingException
     */
    public Object sync_request_on_local(String ifu ,String nes, String reference) throws JsonProcessingException {

        List<BasicNameValuePair> formData = new ArrayList<>();
        formData.add(new BasicNameValuePair("form[ifu]", ifu));
        formData.add(new BasicNameValuePair("form[nes]", nes));
        formData.add(new BasicNameValuePair("form[reference]", reference));

        String url =  AuthRequestValue.E_SINTAXE.getValue()+"rest/asf/details";
        Map<String, Object> response= e_sintax_service.callApi(url, formData);
        saveDocumentRequestToLocal(response.get("data"),reference,nes);
        return  response.get("data");
    }

    /**
     *
     * @param objRequest
     * @param reference
     * @param nes
     * @throws JsonProcessingException
     */
    public  void saveDocumentRequestToLocal(Object objRequest,String reference,String nes) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResult = objectMapper.writeValueAsString(objRequest);

         JsonNode rootNode = objectMapper.readTree(jsonResult);

        String numero_ifu = rootNode.get("items").get("resultat").get("numero_ifu").toString();
        String dateDemande = rootNode.get("items").get("resultat").get("dateDemande").toString();
        String nom_raison_sociale = rootNode.get("items").get("resultat").get("nom_raison_sociale").toString();
        String libelleActivite = rootNode.get("items").get("resultat").get("libelleActivite").toString();
        String adresse = rootNode.get("items").get("resultat").get("adresse").toString();
        String localisation = rootNode.get("items").get("resultat").get("localisation").toString();
        String ville = rootNode.get("items").get("resultat").get("ville").toString();
        String structure_fiscale = rootNode.get("items").get("resultat").get("structure_fiscale").toString();
        String adresse_structure_fiscale = rootNode.get("items").get("resultat").get("adresse_structure_fiscale").toString();

        String dateDelivrance = rootNode.get("items").get("resultat").get("dateDelivrance").toString();
        String dateExpiration = rootNode.get("items").get("resultat").get("dateExpiration").toString();

        DocumentRequest documentRequest = new DocumentRequest();
        documentRequest.setReference(reference);
        documentRequest.setIfu(numero_ifu);
        documentRequest.setNes(nes);
        documentRequest.setRaisonSociale(nom_raison_sociale);
        documentRequest.setDateDelivrance(dateDelivrance);
        documentRequest.setStatut_demande(String.valueOf(DocumentRequestStatus.PROCESSING));
        saveAsf(documentRequestMapper.documentEntityToDto(documentRequest));

        System.out.println("Numéro IFU : " + numero_ifu);
        System.out.println(" nom_raison_sociale : " + nom_raison_sociale);


    }
}
