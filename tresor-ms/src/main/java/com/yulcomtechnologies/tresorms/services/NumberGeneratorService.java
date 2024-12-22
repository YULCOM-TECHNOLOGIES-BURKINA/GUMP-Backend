package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.tresorms.repositories.AttestationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class NumberGeneratorService {
    private final AttestationRepository attestationRepository;
    public String generateNumber() {
        var year = LocalDate.now().getYear();
        var count = attestationRepository.countByYear(year) + 1;
        return String.format("%d-%04d", year, count);
    }
}
