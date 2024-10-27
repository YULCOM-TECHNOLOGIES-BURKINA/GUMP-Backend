package com.yulcomtechnologies.tresorms.dtos;

import java.time.LocalDate;

public record AttestationDto(
    String path,
    String number,
    LocalDate expirationDate
) {
}
