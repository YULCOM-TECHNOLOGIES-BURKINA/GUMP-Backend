package com.yulcomtechnologies.asfms.entities;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_requests")
 public class DocumentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "statut_demande")
    private String statut_demande;

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande= LocalDateTime.now();

    @Column(name = "date_delivrance")
    private String dateDelivrance;

    @Column(name = "ifu")
    private String ifu;

    @Column(name = "nes", nullable = true)
    private String nes;

    @Column(name = "raison_sociale")
    private String raisonSociale;

    @Column(name = "reference")
    private String reference;


    public DocumentRequest(Long id, String statut_demande, LocalDateTime dateDemande, String dateDelivrance, String ifu, String nes, String raisonSociale, String reference) {
        this.id = id;
        this.statut_demande = statut_demande;
        this.dateDemande = dateDemande;
        this.dateDelivrance = dateDelivrance;
        this.ifu = ifu;
        this.nes = nes;
        this.raisonSociale = raisonSociale;
        this.reference = reference;
    }

    public DocumentRequest() {

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

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
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


    @Override
    public String toString() {
        return "DocumentRequest{" +
                "id=" + id +
                ", statut_demande='" + statut_demande + '\'' +
                ", dateDemande=" + dateDemande +
                ", dateDelivrance='" + dateDelivrance + '\'' +
                ", ifu='" + ifu + '\'' +
                ", nes='" + nes + '\'' +
                ", raisonSociale='" + raisonSociale + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}
