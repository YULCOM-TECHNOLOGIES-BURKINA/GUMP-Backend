package com.yulcomtechnologies.drtssms.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ApproveDocumentRequestDto {
    String attestationAnpeNumber;
    String attestationCnssNumber;
    LocalDate attestationAnpeDate;
    LocalDate attestationCnssDate;
}
