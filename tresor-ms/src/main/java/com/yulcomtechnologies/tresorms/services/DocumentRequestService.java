package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.enums.UserRole;
import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.tresorms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.tresorms.dtos.GetDocumentRequestDto;
import com.yulcomtechnologies.tresorms.dtos.PayRequest;
import com.yulcomtechnologies.tresorms.dtos.PaymentRequestResponse;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.entities.Payment;
import com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.tresorms.enums.PaymentStatus;
import com.yulcomtechnologies.tresorms.events.PaymentSucceeded;
import com.yulcomtechnologies.tresorms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.tresorms.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.tresorms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.tresorms.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentRequestService {
    private final DocumentRequestRepository repository;
    private final DocumentRequestMapper documentRequestMapper;
    private final AttestationGenerator attestationGenerator;
    private final AuthenticatedUserService authenticatedUserService;
    private final UsersFeignClient usersFeignClient;
    private final DocumentRequestRepository documentRequestRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationConfigRepository applicationConfigRepository;
    private final EventPublisher eventPublisher;

    public DocumentRequest submitDocumentRequest(DocumentRequestDto documentRequestDto, Boolean isForPublicContract) {
        var documentRequest = new DocumentRequest();
        var authenticatedUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));

        var userData = usersFeignClient.getUsernameOrKeycloakId(authenticatedUser.getKeycloakUserId());
        var company = userData.getCompany();

        BeanUtils.copyProperties(documentRequestDto, documentRequest);
        documentRequest.setIsForPublicContract(true);
        documentRequest.setRccmReference(company.getRccm());
        documentRequest.setIfuNumber(company.getIfu());

        if (documentRequest.getAddress() == null) {
            documentRequest.setAddress(company.getAddress());
        }

        documentRequest.setBusinessDomain(documentRequest.getBusinessDomain());

        if (documentRequest.getAddress() == null) {
            documentRequest.setIsPaid(false);
        }
        documentRequest.setStatus(DocumentRequestStatus.PENDING.toString());
        documentRequest.setRequesterId(authenticatedUser.getKeycloakUserId());
        return repository.save(documentRequest);
    }

    public Page<GetDocumentRequestDto> getPaginatedDocumentRequests(Pageable pageable, String publicContractNumber) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData()
                .orElseThrow(() -> new BadRequestException("User not found"));
        var role = UserRole.valueOf(currentUser.getRole());

        if (role == UserRole.USER) {
            var data = publicContractNumber != null
                    ? repository.findByRequesterIdAndPublicContractNumber(currentUser.getKeycloakUserId(),
                            publicContractNumber, pageable)
                    : repository.findByRequesterId(currentUser.getKeycloakUserId(), pageable);

            return data.map(documentRequestMapper::toDto);
        } else if (role == UserRole.TRESOR_AGENT) {
            var data = publicContractNumber != null
                    ? repository.findByPublicContractNumber(publicContractNumber, publicContractNumber, pageable)
                    : repository.findAll(pageable);

            return data.map(documentRequestMapper::toDto);
        }

        throw new BadRequestException("Cannot get document requests");
    }

    public GetDocumentRequestDto getDocumentRequest(String id) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData()
                .orElseThrow(() -> new BadRequestException("User not found"));
        var role = UserRole.valueOf(currentUser.getRole());
        var document = repository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ResourceNotFoundException("Document request not found"));

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

    public PaymentRequestResponse pay(Long id, PayRequest payRequest) {
        var documentRequest = documentRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document request not found"));

        var price = 1500.0;

        var paymentId = UUID.randomUUID().toString();

        paymentRepository.save(
                Payment.builder()
                        .id(paymentId)
                        .documentRequestId(documentRequest.getId())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .amount(price)
                        .paymentDate(LocalDateTime.now())
                        .status(PaymentStatus.PENDING.toString())
                        .build());

        var url = Base64.getEncoder().encodeToString(payRequest.getCallbackUrl().getBytes());
        log.info("Encoded URL: {}", url);

        log.info("Payment request received for document request with id {}", id);

        return new PaymentRequestResponse(
                String.format(
                        "https://pgw-test.fasoarzeka.bf/AvepayPaymentGatewayUI/avepay-payment/app/validorder?amount=%s&merchantid=%s&securedAccessToken=eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0UDdJNkI0Uzc5IiwiaWF0IjoxNzI5MDA3NTgyLCJzdWIiOiIyMjYwMDAwMDAzMyIsImlzcyI6ImFyemVrYSIsIlBBWUxPQUQiOiJhY2Nlc3NfdG9rZW4iLCJleHAiOjE3OTIwNzk1ODJ9.N_XttQtoOyacQwylkSWR_we5wo96Ise_3vi6O_IJUIXDqenOmWZ0xtczb_FwD2vsgqCzwEK8oxdQs8w3CheWVg&mappedOrderId=%s&linkBackToCallingWebsite=%s",
                        (int) price,
                        356,
                        paymentId,
                        url));

    }

    public void updatePaymentStatus(Long id, String paymentId) {
        var documentRequest = documentRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document request not found"));

        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        // Get payment status from payment gateway
        // If payment is successful, update the document

        if (true) {
            payment.setStatus(PaymentStatus.SUCCEEDED.toString());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            documentRequest.setIsPaid(true);
            documentRequestRepository.save(documentRequest);

            eventPublisher.dispatch(
                    new PaymentSucceeded(
                            paymentId,
                            documentRequest.getId()));
        }
    }

    public void rejectDocumentRequest(Long id) {
        var documentRequest = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document request not found"));

        documentRequest.setStatus(DocumentRequestStatus.REJECTED.toString());
        repository.save(documentRequest);
    }

    public void rollbackRejection(Long id) {
        var documentRequest = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));

        documentRequest.setStatus(DocumentRequestStatus.COMPANY_HAS_DEBT_WAITING_FOR_MANUAL_REVIEW.toString());
        repository.save(documentRequest);
    }
}
