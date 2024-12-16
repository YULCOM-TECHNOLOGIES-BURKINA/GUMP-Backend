package com.yulcomtechnologies.drtssms.controllers;

import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.dtos.UtilisateursDrtssDto;
import com.yulcomtechnologies.drtssms.entities.UtilisateursDrtss;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.services.UtilisateurService;
import com.yulcomtechnologies.sharedlibrary.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/utilisateur_drtss")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UsersFeignClient usersFeignClient;



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

    @GetMapping(path = "/users/{id}")
    public UserDto userFeign(@PathVariable String id) {
        return usersFeignClient.getUser(id);
    }

    @GetMapping("users/{id}/signatory/toggle")
    public ResponseEntity<Void> toglleUserSignatoryState(
            @PathVariable Long id
    ) {
        usersFeignClient.toglleUserSignatoryState(String.valueOf(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("users/{email}/email")
    public  UserDto  findUserByEmail(
            @PathVariable String email
    ) {
        usersFeignClient.findUserByEmail(email);
        return  usersFeignClient.findUserByEmail(email);
    }



}
