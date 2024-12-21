package com.yulcomtechnologies.tresorms.events;

import com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.tresorms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.tresorms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentRequestStatusChangedListener {
    private final UsersFeignClient usersFeignClient;
    private final NotificationFeignClient notificationFeignClient;
    private final DocumentRequestRepository documentRequestRepository;

    @EventListener
    //@Async
    public void handleDocumentRequestStatusChanged(DocumentRequestChanged event) {
        log.info("Document request status changed: " + event.getDocumentRequestId());
        var documentRequest = documentRequestRepository.findById(event.getDocumentRequestId()).orElseThrow();
        log.info("Document request status: " + documentRequest.getStatus());

        var user = usersFeignClient.getUsernameOrKeycloakId(documentRequest.getRequesterId());
        log.info("User: " + user);

        if (documentRequest.getStatus().equals(DocumentRequestStatus.COMPANY_HAS_DEBT_WAITING_FOR_MANUAL_REVIEW.toString())) {
            //Notify managers that a document request need manual review
            return;
        }

        notificationFeignClient.sendNotification(
            user.getEmail(),
            "Statut de demande mis à jour",
            documentRequest.getStatus().equals("APPROVED") ? "Votre demande a été approuvée" : "Votre demande a été rejetée",
            "Statut de demande mis à jour",
            ""
        );
    }
}
