package com.yulcomtechnologies.justicems.services.justiceClient.dtos;

public record CreateDemandeResponse(
    String numero, Long id, String dateRejet, String descriptionRejet, String statutDemande,
    String motifRejet, String acteExpired, String acteDownloaded, String dossier
) {
}
