package com.yulcomtechnologies.drtssms.events;

import com.yulcomtechnologies.drtssms.dtos.UserDto;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.enums.NotificationStatus;
import com.yulcomtechnologies.drtssms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentRequestChangedListenerTest {
    @InjectMocks
    DocumentRequestStatusChangedListener documentRequestStatusChangedListener;

    @Mock
    UsersFeignClient usersFeignClient;

    @Mock
    NotificationFeignClient notificationFeignClient;

    @Mock
    DocumentRequestRepository documentRequestRepository;

    @ParameterizedTest
    @ValueSource(strings = {"APPROVED", "PROCESSING", "PENDING"})
    void notifiesUser(String status) {
        when(documentRequestRepository.findById(1L)).thenReturn(
            Optional.of(DocumentRequest.builder()
                .status(status)
                .requesterId("1")
                .build()
            )
        );

        when(usersFeignClient.getUser("1")).thenReturn(
            UserDto.builder()
                .email("test@email.com").build()
        );

        documentRequestStatusChangedListener.handleDocumentRequestStatusChanged(
            new DocumentRequestChanged(1L)
        );

        verify(notificationFeignClient).sendNotification(
            "test@email.com",
            NotificationStatus.valueOf(status).getSubject(),
            NotificationStatus.valueOf(status).getMessage(),
            NotificationStatus.valueOf(status).getTitle(),
            ""
        );
    }

    @Test
    void notifiesUserOnRejection() {
        var rejectionReason = "A reason";

        when(documentRequestRepository.findById(1L)).thenReturn(
            Optional.of(DocumentRequest.builder()
                .status(DocumentRequestStatus.REJECTED.name())
                .requesterId("1")
                .rejectionReason(rejectionReason)
                .build()
            )
        );

        when(usersFeignClient.getUser("1")).thenReturn(
            UserDto.builder()
                .email("test@email.com").build()
        );

        documentRequestStatusChangedListener.handleDocumentRequestStatusChanged(
            new DocumentRequestChanged(1L)
        );

        verify(notificationFeignClient).sendNotification(
            "test@email.com",
            NotificationStatus.REJECTED.getSubject(),
            NotificationStatus.REJECTED.getMessage().replace("%s", rejectionReason),
            NotificationStatus.REJECTED.getTitle(),
            ""
        );
    }
}
