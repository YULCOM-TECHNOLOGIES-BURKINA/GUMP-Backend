package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.enums.UserRole;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.tresorms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.tresorms.dtos.GetDocumentRequestDto;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.tresorms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.tresorms.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentRequestService {
    private final DocumentRequestRepository repository;
    private final DocumentRequestMapper documentRequestMapper;
    private final AttestationGenerator attestationGenerator;
    private final AuthenticatedUserService authenticatedUserService;
    private final UsersFeignClient usersFeignClient;

    public DocumentRequest submitDocumentRequest(DocumentRequestDto documentRequestDto, Boolean isForPublicContract) {
        var documentRequest = new DocumentRequest();
        var authenticatedUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));

        var userData = usersFeignClient.getUsernameOrKeycloakId(authenticatedUser.getKeycloakUserId());
        var company = userData.getCompany();

        BeanUtils.copyProperties(documentRequestDto, documentRequest);
        documentRequest.setIsForPublicContract(true);
        documentRequest.setRccmReference(company.getRccm());
        documentRequest.setIfuNumber(company.getIfu());
        documentRequest.setOrganizationName(company.getName());
        documentRequest.setIsPaid(false);
        documentRequest.setStatus(DocumentRequestStatus.PENDING.toString());
        documentRequest.setRequesterId(authenticatedUser.getKeycloakUserId());
        return repository.save(documentRequest);
    }

    public Page<GetDocumentRequestDto> getPaginatedDocumentRequests(Pageable pageable) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));
        var role = UserRole.valueOf(currentUser.getRole());

        if (role == UserRole.USER) {
            return repository.findByRequesterId(currentUser.getKeycloakUserId(), pageable).map(documentRequestMapper::toDto);
        }
        else if (role == UserRole.TRESOR_AGENT) {
            return repository.findAll(pageable).map(documentRequestMapper::toDto);
        }

        throw new BadRequestException("Cannot get document requests");
    }

    public GetDocumentRequestDto getDocumentRequest(String id) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));
        var role = UserRole.valueOf(currentUser.getRole());
        var document = repository.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFoundException("Document request not found"));

        if (role.equals(UserRole.TRESOR_AGENT) || document.getRequesterId().equals(currentUser.getKeycloakUserId())) {
            return repository.findById(Long.parseLong(id))
                .map(documentRequestMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Document request not found"));
        }

        throw new BadRequestException("Cannot get document request");
    }

    public void approveDocumentRequest(Long id) throws IOException {
        var documentRequest = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));

        documentRequest.setStatus(DocumentRequestStatus.APPROVED.toString());
        attestationGenerator.generateDocument(id);
        repository.save(documentRequest);
    }
}

