package com.yulcomtechnologies.drtssms.events;

import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.enums.NotificationStatus;
import com.yulcomtechnologies.drtssms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentRequestStatusChangedListener {
    private final UsersFeignClient usersFeignClient;
    private final NotificationFeignClient notificationFeignClient;
    private final DocumentRequestRepository documentRequestRepository;

    @EventListener
    @Async
    public void handleDocumentRequestStatusChanged(DocumentRequestChanged event) {
        log.info("Document request status changed: " + event.getDocumentRequestId());
        var documentRequest = documentRequestRepository.findById(event.getDocumentRequestId()).orElseThrow();
        log.info("Document request status: " + documentRequest.getStatus());

        var user = usersFeignClient.getUsernameOrKeycloakId(documentRequest.getRequesterId());
        log.info("User: " + user);

        var emailData = getEmailData(documentRequest);
        var message = getFormattedMessage(documentRequest, emailData.getMessage());

        notificationFeignClient.sendNotification(
            user.getEmail(),
            emailData.getSubject(),
            message,
            emailData.getTitle(),
            ""
        );
    }

    private String getFormattedMessage(DocumentRequest documentRequest, String message) {
        return switch (DocumentRequestStatus.valueOf(documentRequest.getStatus())) {
            case REJECTED ->String.format(message, documentRequest.getRejectionReason());
            default -> message;
        };
    }

    private NotificationStatus getEmailData(DocumentRequest documentRequest) {
        return switch (DocumentRequestStatus.valueOf(documentRequest.getStatus())) {
            case PROCESSING -> NotificationStatus.PROCESSING;
            case APPROVED -> NotificationStatus.APPROVED;
            case REJECTED -> NotificationStatus.REJECTED;
            case PENDING -> NotificationStatus.PENDING;
        };
    }
}

record EmailData(String subject, String title, String content) {}
