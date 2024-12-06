package com.yulcomtechnologies.justicems.services;

import com.yulcomtechnologies.feignClients.UserMsFeignClient;
import com.yulcomtechnologies.feignClients.dtos.CompanyDto;
import com.yulcomtechnologies.feignClients.dtos.UserDto;
import com.yulcomtechnologies.justicems.entities.DocumentRequest;
import com.yulcomtechnologies.justicems.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.justicems.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserData;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentRequestServiceTest {
    @InjectMocks
    DocumentRequestService documentRequestService;

    @Mock
    FileStorageService fileStorageService;

    @Mock
    AuthenticatedUserService authenticatedUserService;

    @Mock
    DocumentRequestRepository documentRequestRepository;

    @Mock
    FileRepository fileRepository;

    @Mock
    UserMsFeignClient userMsFeignClient;


    @Test
    void requestDocument() throws IOException {
        when(authenticatedUserService.getAuthenticatedUserData()).thenReturn(Optional.of(new AuthenticatedUserData("username", "role", "1")));
        when(userMsFeignClient.getUser("1")).thenReturn(
            UserDto.builder().company(
                new CompanyDto("Yulcom", "123", "Ouaga", "RCCM")
            ).build()
        );

        var documentRequestArgumentCaptor = ArgumentCaptor.forClass(DocumentRequest.class);

        documentRequestService.submitDocumentRequest(
            new MockMultipartFile("extraitRccm", "extraitRccm".getBytes()),
            new MockMultipartFile("statutEntreprise", "statutEntreprise".getBytes()),
            LocalDate.of(2021, 9, 1)
        );

        verify(documentRequestRepository).save(documentRequestArgumentCaptor.capture());

        var documentRequest = documentRequestArgumentCaptor.getValue();

        assertEquals("1", documentRequest.getRequesterId());
        assertEquals("PENDING", documentRequest.getStatus());
        assertEquals("Yulcom", documentRequest.getCompanyName());
        assertEquals("RCCM", documentRequest.getRccm());
    }
}
