package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.PayRequest;
import com.yulcomtechnologies.drtssms.dtos.PaymentRequestResponse;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.drtssms.events.DocumentRequestChanged;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.drtssms.mappers.DocumentRequestMapper;
import com.yulcomtechnologies.drtssms.repositories.ApplicationConfigRepository;
import com.yulcomtechnologies.drtssms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.drtssms.repositories.FileRepository;
import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.enums.UserRole;
import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.sharedlibrary.exceptions.BadRequestException;
import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentRequestService {
    private final DocumentRequestRepository documentRequestRepository;
    private final FileRepository fileRepository;
    private final DocumentRequestMapper documentRequestMapper;
    private final FileStorageService fileStorageService;
    private final AttestationGenerator attestationGenerator;
    private final EventPublisher eventPublisher;
    private final ApplicationConfigRepository applicationConfigRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final UsersFeignClient usersFeignClient;
    private final ApplicationEventPublisher eventPublish;

    public DocumentRequest submitDocumentRequest(
        MultipartFile attestationCnss, MultipartFile attestationAnpe,
        String publicContractNumber, Boolean isForPublicContract,
        String contractPurpose, String contractingOrganizationName
    ) throws IOException {
        File cnssAttestation = saveFile(attestationCnss, "Attestation CNSS");
        File anpeAttestation = saveFile(attestationAnpe, "Attestation ANPE");
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));
        log.info("currentUser: {}", currentUser);
        var userData = usersFeignClient.getUsernameOrKeycloakId(currentUser.getKeycloakUserId());
        log.info("userData: {}", userData);


        var documentRequest = DocumentRequest.builder()
            .requesterId(currentUser.getKeycloakUserId())
            .isPaid(false)
            .contractPurpose(contractPurpose)
            .contractingOrganizationName(contractingOrganizationName)
            .region(userData.getRegion())
            .isForPublicContract(isForPublicContract)
            .createdAt(LocalDateTime.now())
            .publicContractNumber(publicContractNumber)
            .status(DocumentRequestStatus.PENDING.name()).build();

        // Set the files in the document request
        Set<File> files = new HashSet<>();
        files.add(cnssAttestation);
        files.add(anpeAttestation);
        documentRequest.setFiles(files);
       // eventPublisher.dispatch(new DocumentRequestChanged(7L));

        return documentRequestRepository.save(documentRequest);

    }

    private File saveFile(MultipartFile file, String label) throws IOException {
        String UPLOAD_DIR = "uploads/";
        Path filePath = Paths.get(UPLOAD_DIR, UUID.randomUUID() + "-" + file.getOriginalFilename());
        fileStorageService.saveFile(file.getBytes(), filePath.toString());

        File fileEntity = new File(label, filePath.toString());

        return fileRepository.save(fileEntity);
    }

    public Page<DocumentRequestDto> getPaginatedDocumentRequests(Pageable pageable) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));
        var role = UserRole.valueOf(currentUser.getRole());
        var userData = usersFeignClient.getUsernameOrKeycloakId(currentUser.getKeycloakUserId());

        log.info("Role: {}", role);

        if (role == UserRole.ADMIN) {
            return documentRequestRepository.findAll(pageable).map(
                documentRequest -> documentRequestMapper.toDto(
                    documentRequest,
                    applicationConfigRepository.get()
                )
            );
        }

        if (role == UserRole.USER) {
            return documentRequestRepository.findAllByRequesterId(currentUser.getKeycloakUserId(), pageable).map(
                documentRequest -> documentRequestMapper.toDto(
                    documentRequest,
                    applicationConfigRepository.get()
                )
            );
        }

        if (role == UserRole.DRTSS_AGENT || role == UserRole.DRTSS_REGIONAL_MANAGER) {
            return documentRequestRepository.findAllByRegion(userData.getRegion(), pageable).map(
                documentRequest -> documentRequestMapper.toDto(
                    documentRequest,
                    applicationConfigRepository.get()
                )
            );
        }

        //Later throw 401
        throw new BadRequestException("Vous ne pouvez pas accéder à cette ressource");
    }

    public DocumentRequestDto getDocumentRequest(String id) {
        return documentRequestRepository.findById(Long.parseLong(id))
            .map(documentRequest -> documentRequestMapper.toDto(
                documentRequest,
                applicationConfigRepository.get()
            ))
            .orElseThrow(() -> new IllegalArgumentException("Document request not found"));
    }

    public void reviewDocumentRequest(
        Long id,
        DocumentRequestStatus status,
        String rejectionReason
    ) {
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));

        if (status == DocumentRequestStatus.REJECTED && rejectionReason == null) {
            throw new BadRequestException("Vous devenez fournir un motif de rejet");
        }

        DocumentRequest documentRequest = documentRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("DocumentRequest not found"));

        documentRequest.setStatus(status.name());
        documentRequest.setRejectionReason(rejectionReason);
        documentRequest.setReviewedBy(currentUser.getKeycloakUserId());

        documentRequestRepository.save(documentRequest);
        eventPublisher.dispatch(new DocumentRequestChanged(documentRequest.getId()));
    }

    public void approveDocumentRequest(Long id, ApproveDocumentRequestDto approveDocumentRequestDto) throws IOException {
        log.info("Approving document request with id {}", id);
        var currentUser = authenticatedUserService.getAuthenticatedUserData().orElseThrow(() -> new BadRequestException("User not found"));


        DocumentRequest documentRequest = documentRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("DocumentRequest not found"));

        if (documentRequest.isApproved()) {
            throw new BadRequestException("Document déjà approuvé");
        }

        if (!documentRequest.getIsPaid()) {
            //throw new BadRequestException("Document non payé");
        }

        documentRequest.setStatus(DocumentRequestStatus.APPROVED.name());
        documentRequest.setApprovedBy(currentUser.getKeycloakUserId());

        attestationGenerator.generateDocument(
            approveDocumentRequestDto,
            documentRequest.getId()
        );

        documentRequestRepository.save(documentRequest);
        eventPublisher.dispatch(new DocumentRequestChanged(documentRequest.getId()));
    }

    public PaymentRequestResponse pay(Long id, PayRequest payRequest) {
        /*var documentRequest = documentRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document request not found"));*/

        var paymentId = UUID.randomUUID().toString();


        var url = Base64.getEncoder().encodeToString(payRequest.getCallbackUrl().getBytes());
        log.info("Encoded URL: {}", url);

        log.info("Payment request received for document request with id {}", id);
        return new PaymentRequestResponse(
            String.format(
                "https://pgw-test.fasoarzeka.bf/AvepayPaymentGatewayUI/avepay-payment/app/validorder?amount=%s&merchantid=%s&securedAccessToken=eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiI0UDdJNkI0Uzc5IiwiaWF0IjoxNzI5MDA3NTgyLCJzdWIiOiIyMjYwMDAwMDAzMyIsImlzcyI6ImFyemVrYSIsIlBBWUxPQUQiOiJhY2Nlc3NfdG9rZW4iLCJleHAiOjE3OTIwNzk1ODJ9.N_XttQtoOyacQwylkSWR_we5wo96Ise_3vi6O_IJUIXDqenOmWZ0xtczb_FwD2vsgqCzwEK8oxdQs8w3CheWVg&mappedOrderId=%s&linkBackToCallingWebsite=%s",
                1500,
                356,
                paymentId,
                url
            )
        );

    }

    public void updatePaymentStatus(Long id, String paymentId) {
        var documentRequest = documentRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document request not found"));

        documentRequest.setIsPaid(true);
        documentRequestRepository.save(documentRequest);
        eventPublisher.dispatch(new DocumentRequestChanged(documentRequest.getId()));
    }
}

