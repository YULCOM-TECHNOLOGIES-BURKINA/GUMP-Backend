package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.UtilisateursDrtssDto;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.services.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/utilisateur_drtss")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;



    @Operation(summary = "Creer un Utilisateurs ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lire Fichier",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UtilisateursDrtss.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid  File",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Donnees Invalide",
                    content = @Content)})
    @PostMapping(path = "/save")
    public UtilisateursDrtss createUtilisateur(@RequestBody UtilisateursDrtssDto utilisateursDrtssDto){
            return utilisateurService.createUtilisateur(utilisateursDrtssDto);
    }

    @Operation(summary = "Liste des Utilisateurs ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " List",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UtilisateursDrtss.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid  File",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Donnees Invalide",
                    content = @Content)})

    @GetMapping("/liste")
    public Page<UtilisateursDrtss> getUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return utilisateurService.listUtilisateurs(page,size);
    }

    @PostMapping(path = "/update_status")
    public UtilisateursDrtss update_status(@RequestBody UtilisateursDrtssDto utilisateursDrtssDto){
        return utilisateurService.update_status(utilisateursDrtssDto.getId());
    }
}