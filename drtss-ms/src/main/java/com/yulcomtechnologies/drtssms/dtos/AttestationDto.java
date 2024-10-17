package com.yulcomtechnologies.drtssms.dtos;

import java.time.LocalDate;

public record AttestationDto(
    String path,
    String number,
    LocalDate expirationDate
) {
}
