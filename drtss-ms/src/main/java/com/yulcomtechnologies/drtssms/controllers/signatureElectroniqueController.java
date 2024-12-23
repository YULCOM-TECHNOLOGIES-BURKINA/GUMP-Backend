package com.yulcomtechnologies.drtssms.controllers;

import com.itextpdf.io.IOException;
import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.FileDto;
import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.services.SignatureDocumentService;
import com.yulcomtechnologies.drtssms.services.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/signature_electronique")
public class signatureElectroniqueController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private SignatureDocumentService signatureDocumentService;



    @GetMapping("/signataire/{email}")
    public ResponseEntity<SignatureScanner> getSignatoryByEmail(@PathVariable String email) {
        var signaturory = signatureDocumentService.getSignatoryByEmail(email);
        return ResponseEntity.ok(signaturory);
    }

    /**
     * Creer un signataire ,personne pouvant signer
     * @param file
     * @param userId
     * @return
     */


    @PostMapping(path = "/create_signataire", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createSignataire(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (file.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Le fichier est vide.");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérifie les types de fichiers
            String contentType = file.getContentType();
            if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
                response.put("status", "error");
                response.put("message", "Type de fichier non pris en charge. Veuillez télécharger une image PNG ou JPEG.");
                return ResponseEntity.badRequest().body(response);
            }

            // Sauvegarde le signataire
            SignatureScanner savedSignature = signatureDocumentService.createSignatory(file, (long) userId);

            response.put("status", "success");
            response.put("message", "Signataire enregistré avec succès.");
            response.put("signataireId", savedSignature.getId());
            response.put("signataireInfo", savedSignature);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du traitement du fichier : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Échec du téléchargement de l'image : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    /**
     * Signer attestation DRTPS
     * @param attestationPath
     * @param signatoryId
     * @param id
     * @return
     */
    @PostMapping(path = "/sign_attestation", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> signAttestation(
            @RequestParam("attestationPath") String attestationPath,
            @RequestParam("signatoryId") Long signatoryId,
            @RequestParam("requestId") Long id
    ) {

        try {
            return signatureDocumentService.signAttestation2(attestationPath, signatoryId,id);
        } catch (IllegalArgumentException e) {
            String errorResponse = "{\"error\": \"" + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.getBytes());
        } catch (Exception e) {
            String errorResponse = "{\"error\": \"Erreur inattendue : " + e.getMessage() + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.getBytes());
        }
    }




    /**
     * Liste des Signataires
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/liste_signataire")
    public Page<SignatureScanner> getUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return signatureDocumentService.listSignatory(page,size);
    }




    @GetMapping("download_certificate/signatoryId")
    public File downloadCertificate(@RequestParam("signatoryId") Long signatoryId) throws java.io.IOException {
        HttpHeaders headers = new HttpHeaders();



        File  file= signatureDocumentService.getSignatoryCertificat(signatoryId);



        return  file;
    }
    /**
     * Telechager le Certificat
     * @param path
     * @return
     */
    @GetMapping("download_certificate")
    public ResponseEntity<Resource> downloadCertificate(@RequestParam("path") String path) {

        Path filePath = Paths.get(path);
        File file = filePath.toFile();

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        Resource resource = new FileSystemResource(file);
        String contentType = "application/octet-stream";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    /**
     *
     * @param id
     * @return
     */
    @PostMapping("toggle_status/{id}")
    public ResponseEntity<SignatureScanner> toggleSignatoryStatus(@PathVariable Long id) {
        try {
            SignatureScanner updatedSignatory = signatureDocumentService.updateSignatoryStatus(id);
            return ResponseEntity.ok(updatedSignatory);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    /**
     * Conversion du fichier multipart en fichier
     *
     * @param file
     * @return
     */
    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try {
            file.transferTo(convFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Échec de la conversion du fichier multipart en fichier", e);
        }
        return convFile;
    }






}
