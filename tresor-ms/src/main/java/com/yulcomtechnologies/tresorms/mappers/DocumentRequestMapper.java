package com.yulcomtechnologies.tresorms.mappers;

import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import com.yulcomtechnologies.tresorms.dtos.AttestationDto;
import com.yulcomtechnologies.tresorms.dtos.GetDocumentRequestDto;
import com.yulcomtechnologies.tresorms.entities.ApplicationConfig;
import com.yulcomtechnologies.tresorms.entities.DocumentRequest;
import com.yulcomtechnologies.tresorms.enums.RequestType;
import com.yulcomtechnologies.tresorms.repositories.ApplicationConfigRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
@AllArgsConstructor
public class DocumentRequestMapper {
    private final FileStorageService fileStorageService;
    private final ApplicationConfigRepository applicationConfigRepository;

    public GetDocumentRequestDto toDto(DocumentRequest documentRequest) {
        var dto = new GetDocumentRequestDto();
        dto.setId(documentRequest.getId().toString());
        dto.setCreatedAt(documentRequest.getCreatedAt());

        var applicationConfig = applicationConfigRepository.get();

        int processingTime = documentRequest.getRequestType().equals(RequestType.LIQUIDATION) ? applicationConfig.getProcessingTimeInDaysForLiquidation() : applicationConfig.getProcessingTimeInDaysForSoumission();

        dto.setRemainingDaysBeforeDueDate(
            (int) LocalDateTime.now().until(
                addDaysExcludingWeekends(documentRequest.getCreatedAt().toLocalDate(), processingTime).atStartOfDay(),
                ChronoUnit.DAYS
            )
        );

        BeanUtils.copyProperties(documentRequest, dto);

        if (documentRequest.isApproved()) {
            var attestation = documentRequest.getAttestation();

            dto.setAttestation(
                new AttestationDto(
                    fileStorageService.getPath(attestation.getFile()),
                    attestation.getNumber(),
                    attestation.getExpirationDate().toLocalDate()
                )
            );
        }

        return dto;
    }

    private LocalDate addDaysExcludingWeekends(LocalDate startDate, int daysToAdd) {
        LocalDate date = startDate;
        int addedDays = 0;

        while (addedDays < daysToAdd) {
            date = date.plusDays(1);
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                addedDays++;
            }
        }

        return date;
    }
}
