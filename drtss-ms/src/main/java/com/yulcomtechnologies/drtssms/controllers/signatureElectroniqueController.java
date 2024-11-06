package com.yulcomtechnologies.drtssms.controllers;

import com.itextpdf.io.IOException;
import com.yulcomtechnologies.drtssms.dtos.FileDto;
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

@AllArgsConstructor
@RestController
@RequestMapping("/signature_electronique")
public class signatureElectroniqueController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private SignatureDocumentService signatureDocumentService;


    /**
     * Creer un signataire ,personne pouvant signer
     * @param file
     * @param userId
     * @return
     */
    @PostMapping(path = "/create_signataire", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createSignataire(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Le fichier est vide.");
            }
            //  uniquement les images PNG ou JPEG
            String contentType = file.getContentType();
            if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType)) {
                return ResponseEntity.badRequest().body("Type de fichier non pris en charge. Veuillez télécharger une image PNG ou JPEG.");
            }

            SignatureScanner savedSignature = signatureDocumentService.createSignatory(file, (long) userId);
            return ResponseEntity.ok().body("Image téléchargée avec succès. ID de la signature : " + savedSignature.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du traitement du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec du téléchargement de l'image : " + e.getMessage());
        }
    }


    /**
     *Signe attestation
     * @param attestationPath
     * @param signatoryId
     * @param keyStore
     * @param keyStorePassword
     * @param alias
     * @return
     */

    @PostMapping(path = "/sign_attestation", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> signDocumentActe(
            @RequestParam("attestationPath") String attestationPath,
            @RequestParam("signatoryId") Long signatoryId,
            @RequestParam("keyStore") MultipartFile keyStore,
            @RequestParam(value = "keyStorePassword", defaultValue = "gump123") String keyStorePassword,
            @RequestParam(value = "x",defaultValue = "70") float x,
            @RequestParam(value = "y",defaultValue = "85") float y,
            @RequestParam(value = "alias", defaultValue = "mykey") String alias) {

        File keyStoreFile = null;
        try {

            keyStoreFile = convertMultiPartToFile(keyStore);

           return   signatureDocumentService.signAttestation(attestationPath, signatoryId, keyStoreFile, keyStorePassword, alias,x,y);

         } catch (Exception e) {
            return null;
        } finally {
            // Supprimer le fichier temporaire
            if (keyStoreFile != null && keyStoreFile.exists()) {
                keyStoreFile.delete();
            }
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

    @PostMapping(path = "/download_certificate", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Resource> loadFile(@RequestBody FileDto fileDto) {
        Path path = Paths.get(fileDto.getPath());

        try {
            File file = signatureDocumentService.loadFileByPath(path);

            // Fichier introuvable ou non lisible
            if (!file.exists() || !file.canRead()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // Erreur d'accès au fichier
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
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
