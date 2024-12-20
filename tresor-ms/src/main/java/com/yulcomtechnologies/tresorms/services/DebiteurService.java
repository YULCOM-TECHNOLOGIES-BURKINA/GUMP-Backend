package com.yulcomtechnologies.tresorms.services;

import com.yulcomtechnologies.tresorms.dtos.DebiteurDTO;
import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import com.yulcomtechnologies.tresorms.mappers.DebiteurMapper;
import com.yulcomtechnologies.tresorms.repositories.DebiteurRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DebiteurService {

    @Autowired
    private DebiteurRepository debiteurRepository;

    @Autowired
    private DebiteurMapper debiteurMapper;

    public static boolean isRowEmpty(Row row) {
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public List<DebiteurEntity> importAndSaveExcel(MultipartFile file) {
        log.info("Importing and saving Excel file: {}", file.getOriginalFilename());
        List<DebiteurDTO> dtos = parseExcel(file);
        validateData(dtos);

        List<DebiteurEntity> savedEntities = new ArrayList<>();

        for (DebiteurDTO dto : dtos) {
            DebiteurEntity entity = upsertDebiteur(dto);
            savedEntities.add(entity);
        }

        log.info("Successfully processed {} debiteurs (updates and inserts)", savedEntities.size());
        return savedEntities;
    }

    @Transactional
    public DebiteurEntity upsertDebiteur(DebiteurDTO dto) {
        if (dto.getNumeroIFU() == null || dto.getNumeroIFU().trim().isEmpty()) {
            throw new IllegalArgumentException("Numero IFU cannot be null or empty");
        }

        Optional<DebiteurEntity> existingDebiteur = debiteurRepository.findByNumeroIFU(dto.getNumeroIFU());

        if (existingDebiteur.isPresent()) {
            // Update existing entity
            DebiteurEntity existing = existingDebiteur.get();
            updateExistingEntity(existing, dto);
            log.info("Updated existing debiteur with IFU: {}", dto.getNumeroIFU());
            return debiteurRepository.save(existing);
        } else {
            // Create new entity
            DebiteurEntity newEntity = debiteurMapper.toEntity(dto);
            log.info("Created new debiteur with IFU: {}", dto.getNumeroIFU());
            return debiteurRepository.save(newEntity);
        }
    }

    private void updateExistingEntity(DebiteurEntity existing, DebiteurDTO dto) {
        existing.setDebiteur(dto.getDebiteur());
        existing.setPromoteur(dto.getPromoteur());
        existing.setNumeroImmatriculation(dto.getNumeroImmatriculation());
        existing.setRegistreCommerce(dto.getRegistreCommerce());
        existing.setContacts(dto.getContacts());
        existing.setDateNaissance(dto.getDateNaissance());
        existing.setNumeroCNIB(dto.getNumeroCNIB());
        existing.setNumeroCheque(dto.getNumeroCheque());
        existing.setMontantDu(dto.getMontantDu());
    }

    private List<DebiteurDTO> parseExcel(MultipartFile file) {
        log.info("Parsing Excel file: {}", file.getOriginalFilename());
        List<DebiteurDTO> debiteurs = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Skip header row
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (isRowEmpty(row)) {
                    continue;
                }

                DebiteurDTO dto = parseRow(row);
                if (dto != null) {
                    debiteurs.add(dto);
                }
            }

            log.info("Successfully parsed {} debiteurs from Excel", debiteurs.size());
            return debiteurs;

        } catch (Exception e) {
            log.error("Error parsing Excel file: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private DebiteurDTO parseRow(Row row) {
        try {
            DebiteurDTO dto = new DebiteurDTO();

            dto.setDebiteur(getCellStringValue(row.getCell(0)));
            dto.setPromoteur(getCellStringValue(row.getCell(1)));
            dto.setNumeroIFU(getCellStringValue(row.getCell(2)));
            dto.setNumeroImmatriculation(getCellStringValue(row.getCell(3)));
            dto.setRegistreCommerce(getCellStringValue(row.getCell(4)));
            dto.setContacts(getCellStringValue(row.getCell(5)));
            dto.setDateNaissance(getCellStringValue(row.getCell(6)));
            dto.setNumeroCNIB(getCellStringValue(row.getCell(7)));
            dto.setNumeroCheque(getCellStringValue(row.getCell(8)));
            dto.setMontantDu(getCellNumericValue(row.getCell(9)));

            return dto;
        } catch (Exception e) {
            log.warn("Error parsing row {}: {}", row.getRowNum(), e.getMessage());
            return null;
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("dd-MM-yyyy").format(cell.getDateCellValue());
                }
                return String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            default -> {
                return null;
            }
        }
    }

    private Double getCellNumericValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
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
}
