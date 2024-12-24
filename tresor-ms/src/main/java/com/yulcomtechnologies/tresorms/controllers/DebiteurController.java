package com.yulcomtechnologies.tresorms.controllers;

import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import com.yulcomtechnologies.tresorms.repositories.DebiteurRepository;
import com.yulcomtechnologies.tresorms.services.DebiteurService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/debiteurs")
@Slf4j
@AllArgsConstructor
public class DebiteurController {
    private final DebiteurService debiteurService;
    private final DebiteurRepository debiteurRepository;

    @PostMapping("/import")
    public ResponseEntity<Void> importCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            var extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1);

            if (!extension.endsWith("xlsx") && !extension.endsWith("xls")) {
                return ResponseEntity.badRequest().build();
            }

            debiteurService.importAndSaveExcel(file);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Validation error during CSV import: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error importing CSV: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to import CSV file");
        }
    }

    @GetMapping
    public ResponseEntity<Page<DebiteurEntity>> getAllDebiteurs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<DebiteurEntity> debiteurs = debiteurRepository.findAll(pageable);
        return ResponseEntity.ok(debiteurs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebiteurEntity> getDebiteurById(@PathVariable Long id) {
        return debiteurRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebiteurEntity> updateDebiteur(
        @PathVariable Long id,
        @Valid @RequestBody DebiteurEntity debiteur
    ) {

        debiteur.setId(id);
        debiteurRepository.save(debiteur);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<DebiteurEntity> createDebiteur(@Valid @RequestBody DebiteurEntity debiteur) {
        debiteurRepository.save(debiteur);
        return ResponseEntity.ok(debiteur);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
