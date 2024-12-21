package com.yulcomtechnologies.tresorms.events;

import com.yulcomtechnologies.sharedlibrary.events.EventPublisher;
import com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus;
import com.yulcomtechnologies.tresorms.repositories.DebiteurRepository;
import com.yulcomtechnologies.tresorms.repositories.DocumentRequestRepository;
import com.yulcomtechnologies.tresorms.services.AttestationGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import static com.yulcomtechnologies.tresorms.enums.DocumentRequestStatus.COMPANY_HAS_DEBT_WAITING_FOR_MANUAL_REVIEW;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentSuccessListener {
    private final DocumentRequestRepository documentRequestRepository;
    private final DebiteurRepository debiteurRepository;
    private final AttestationGenerator attestationGenerator;
    private final EventPublisher eventPublisher;

    @EventListener
    //@Async
    public void handle(PaymentSucceeded event) {
        log.info("Payment succeeded: " + event.getPaymentId());
        log.info("Document request id: " + event.getDocumentRequestId());


        var documentRequest = documentRequestRepository.findById(event.getDocumentRequestId()).orElseThrow();

        var totalDebt = debiteurRepository.findTotalDebtByIfu(documentRequest.getIfuNumber());

        if (totalDebt == null || totalDebt == 0.0) {
            log.info("No debt for this company");

            attestationGenerator.generateDocument(documentRequest.getId());
            documentRequest.setStatus(DocumentRequestStatus.APPROVED.toString());
            eventPublisher.dispatch(new DocumentRequestChanged(documentRequest.getId()));
        }
        else {
            log.info("Debt found for this company");
            documentRequest.setStatus(COMPANY_HAS_DEBT_WAITING_FOR_MANUAL_REVIEW.toString());
            eventPublisher.dispatch(new DocumentRequestChanged(documentRequest.getId()));
        }
    }
}
