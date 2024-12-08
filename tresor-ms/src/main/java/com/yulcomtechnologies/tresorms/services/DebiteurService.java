package com.yulcomtechnologies.tresorms.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.yulcomtechnologies.tresorms.dtos.DebiteurDTO;
import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import com.yulcomtechnologies.tresorms.mappers.DebiteurMapper;
import com.yulcomtechnologies.tresorms.repositories.DebiteurRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DebiteurService {

    @Autowired
    private DebiteurRepository debiteurRepository;

    @Autowired
    private DebiteurMapper debiteurMapper;

    @Transactional
    public void importAndSaveCSV(MultipartFile file) {
        List<DebiteurDTO> dtos = parseCSV(file);
        validateData(dtos);

        List<DebiteurEntity> entities = dtos.stream()
            .map(debiteurMapper::toEntity)
            .collect(Collectors.toList());

        debiteurRepository.saveAll(entities);
    }

    private void validateData(List<DebiteurDTO> dtos) {
        for (int i = 0; i < dtos.size(); i++) {
            DebiteurDTO dto = dtos.get(i);
            if (dto.getMontantDu() != null && dto.getMontantDu() < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid montant du at row %d: amount cannot be negative", i + 2)
                );
            }
        }
    }

    private List<DebiteurDTO> parseCSV(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Skip the header line
            reader.readLine();

            CsvToBean<DebiteurDTO> csvToBean = new CsvToBeanBuilder<DebiteurDTO>(reader)
                .withType(DebiteurDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .withSeparator(',')
                .withQuoteChar('"')
                .build();

            List<DebiteurDTO> debiteurs = csvToBean.parse();
            log.info("Successfully parsed {} debiteurs from CSV", debiteurs.size());
            return debiteurs;

        } catch (Exception e) {
            log.error("Error parsing CSV file: {}", e.getMessage());
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }
}

