package com.yulcomtechnologies.asfms.dtos;

public class DocumentRequestDto {

    private Long id;
    private String statut_demande;
    private String dateDemande;
    private String dateDelivrance;
    private String ifu;
    private String nes;
    private String raisonSociale;
    private String reference;

    public DocumentRequestDto(Long id, String statut_demande, String dateDemande, String dateDelivrance, String ifu, String nes, String raisonSociale, String reference) {
        this.id = id;
        this.statut_demande = statut_demande;
        this.dateDemande = dateDemande;
        this.dateDelivrance = dateDelivrance;
        this.ifu = ifu;
        this.nes = nes;
        this.raisonSociale = raisonSociale;
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatut_demande() {
        return statut_demande;
    }

    public void setStatut_demande(String statut_demande) {
        this.statut_demande = statut_demande;
    }

    public String getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(String dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(String dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
    }

    public String getIfu() {
        return ifu;
    }

    public void setIfu(String ifu) {
        this.ifu = ifu;
    }

    public String getNes() {
        return nes;
    }

    public void setNes(String nes) {
        this.nes = nes;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
