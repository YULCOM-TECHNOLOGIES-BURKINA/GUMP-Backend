package com.yulcomtechnologies.drtssms.services;

import com.yulcomtechnologies.drtssms.dtos.ApproveDocumentRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Service
@AllArgsConstructor
public class AttestationGenerator {
    public void generateDocument(
        ApproveDocumentRequestDto approveDocumentRequestDto
    ) {

    }
}
