package com.yulcomtechnologies.drtssms.controllers;

import com.itextpdf.io.IOException;
import com.yulcomtechnologies.drtssms.dtos.UtilisateursDrtssDto;
import com.yulcomtechnologies.drtssms.entities.SignatureScanner;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.services.SignatureDocumentService;
import com.yulcomtechnologies.drtssms.services.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/signature_electronique")
public class signatureElectroniqueController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private SignatureDocumentService signatureDocumentService;


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

            SignatureScanner savedSignature = signatureDocumentService.createSignataire(file, (long) userId);
            return ResponseEntity.ok().body("Image téléchargée avec succès. ID de la signature : " + savedSignature.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du traitement du fichier : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec du téléchargement de l'image : " + e.getMessage());
        }
    }


    @Operation(summary = "Liste des Signataires ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " List",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignatureScanner.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Donnees Invalide",
                    content = @Content)})

    @GetMapping("/liste_signataire")
    public Page<SignatureScanner> getUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return signatureDocumentService.listeSignataire(page,size);
    }
}
