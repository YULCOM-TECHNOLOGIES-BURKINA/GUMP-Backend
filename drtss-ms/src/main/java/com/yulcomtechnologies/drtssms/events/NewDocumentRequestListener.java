package com.yulcomtechnologies.drtssms.events;

import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.enums.NotificationStatus;
import com.yulcomtechnologies.drtssms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.sharedlibrary.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NewDocumentRequestListener {
    private final UsersFeignClient usersFeignClient;
    private final NotificationFeignClient notificationFeignClient;
    private final DocumentRequestRepository documentRequestRepository;

    @EventListener
    @Async
    public void handleDocumentRequestStatusChanged(NewDocumentRequest event) {
        log.info("Mon test de notification: ");

       // var user = usersFeignClient.getUsernameOrKeycloakId(documentRequest.getRequesterId());
       // log.info("User: " + user);
     List<UserDto>  userDtrss=usersFeignClient.getUserByType(UserType.DRTSS_USER);

     userDtrss.forEach(userDto -> {

         var emailData = getEmailData();
         var message = emailData.getMessage();

         notificationFeignClient.sendNotification(
                 userDto.getEmail(),
                 emailData.getSubject(),
                 message,
                 emailData.getTitle(),
                 ""
         );

     });

    }

    private String getFormattedMessage(DocumentRequest documentRequest, String message) {
        return switch (DocumentRequestStatus.valueOf(documentRequest.getStatus())) {
            case REJECTED ->String.format(message, documentRequest.getRejectionReason());
            default -> message;
        };
    }

    private NotificationStatus getEmailData() {
         return    NotificationStatus.NOTIFY_PENDING_REQUEST;
        };

}

