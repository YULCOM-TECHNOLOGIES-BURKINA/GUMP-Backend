package com.yulcomtechnologies.drtssms.mappers;

import com.yulcomtechnologies.drtssms.dtos.AttestationDto;
import com.yulcomtechnologies.drtssms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.drtssms.dtos.FileDto;
import com.yulcomtechnologies.drtssms.entities.ApplicationConfig;
import com.yulcomtechnologies.drtssms.entities.DocumentRequest;
import com.yulcomtechnologies.drtssms.entities.File;
import com.yulcomtechnologies.drtssms.feignClients.UsersFeignClient;
import com.yulcomtechnologies.sharedlibrary.services.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentRequestMapper {
    private final FileStorageService fileStorageService;
    private final UsersFeignClient usersFeignClient;


    public DocumentRequestDto toDto(
        DocumentRequest documentRequest,
        ApplicationConfig applicationConfig
    ) {
        DocumentRequestDto dto = new DocumentRequestDto();
        BeanUtils.copyProperties(documentRequest, dto);
        dto.setId(documentRequest.getId().toString());
        dto.setRequesterId(documentRequest.getRequesterId());
        dto.setStatus(documentRequest.getStatus());
        dto.setReviewedBy(documentRequest.getReviewedBy());
        dto.setApprovedBy(documentRequest.getApprovedBy());
        dto.setCreatedAt(documentRequest.getCreatedAt());
        dto.setIsPaid(documentRequest.getIsPaid());
        dto.setCompany(usersFeignClient.getUsernameOrKeycloakId(documentRequest.getRequesterId()).getCompany());
        dto.setIsPastDue(
            LocalDateTime.now().isAfter(
                addDaysExcludingWeekends(documentRequest.getCreatedAt().toLocalDate(), applicationConfig.getProcessingTimeInDays()).atStartOfDay()
            )
        );

        dto.setRemainingDaysBeforeDueDate(
            (int) LocalDateTime.now().until(
                addDaysExcludingWeekends(documentRequest.getCreatedAt().toLocalDate(), applicationConfig.getProcessingTimeInDays()).atStartOfDay(),
                ChronoUnit.DAYS
            )
        );

        if (documentRequest.isApproved()) {
            var attestation = documentRequest.getAttestation();

            dto.setAttestation(
                new AttestationDto(
                        fileStorageService.getPath(attestation.getFile()),
                    attestation.getNumber(),
                    attestation.getExpirationDate().toLocalDate(), attestation.getFile().getPath()
                )
            );
        }

        if (documentRequest.getFiles() != null) {
            dto.setFiles(documentRequest.getFiles().stream()
                .map(this::fileToDto)
                .collect(Collectors.toSet()));
        }

        return dto;
    }

    public FileDto fileToDto(File file) {
        FileDto fileDto = new FileDto();
         fileDto.setLabel(file.getLabel());
        fileDto.setPath(fileStorageService.getPath(file));
        return fileDto;
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
