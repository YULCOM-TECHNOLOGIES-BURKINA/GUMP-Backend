package com.yulcomtechnologies.drtssms.enums;

public enum NotificationStatus {
    NOTIFY_PENDING_REQUEST("Demande de Document en Attente", "Nouvelle Demande Reçue", "Une nouvelle demande de document a été soumise et attend un traitement"),
    PENDING("Demande de Document en Attente", "Demande de Document en Attente", "Votre demande de document est en attente"),
    REJECTED("Demande de Document Rejetée", "Demande de Document Rejetée", "Votre demande de document a été rejetée pour le motif suivant: %s"),
    PROCESSING("Demande de Document en Cours de Traitement", "Demande de Document en Cours de Traitement", "Votre demande de document est en cours de traitement"),
    APPROVED("Demande de Document Approuvée", "Demande de Document Approuvée", "Votre demande de document a été approuvée"),
    SIGNED("Demande de Document Valider", "Demande de Document Valider", "Votre demande de document a été validée");

    private final String subject;
    private final String title;
    private final String message;

    NotificationStatus(String subject, String title, String message) {
        this.subject = subject;
        this.title = title;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
