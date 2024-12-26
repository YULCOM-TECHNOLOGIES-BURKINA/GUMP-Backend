package com.yulcomtechnologies.tresorms.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yulcomtechnologies.tresorms.BaseIntegrationTest;
import com.yulcomtechnologies.tresorms.entities.DebiteurEntity;
import com.yulcomtechnologies.tresorms.repositories.DebiteurRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DebiteurControllerTest extends BaseIntegrationTest {
    @Autowired
    private DebiteurRepository debiteurRepository;

    @BeforeEach
    void setUp() {
        debiteurRepository.deleteAll();
    }

    @Test
    @Transactional
    void importCSV_Success() throws Exception {
        byte[] fileContent;
        fileContent = DebiteurControllerTest.class.getClassLoader().getResourceAsStream("tests.xlsx").readAllBytes();

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            "text/csv",
            fileContent
        );

        mockMvc.perform(multipart("/debiteurs/import")
                .file(file))
            .andExpect(status().isOk());

        // Verify database state
        List<DebiteurEntity> savedDebiteurs = debiteurRepository.findAll();
        assertEquals(2, savedDebiteurs.size());

        DebiteurEntity firstDebiteur = savedDebiteurs.get(0);
        assertEquals("John Doe", firstDebiteur.getDebiteur());
        assertEquals("ABC Corp", firstDebiteur.getPromoteur());
        assertEquals("123456", firstDebiteur.getNumeroIFU());
        assertEquals(500_000, firstDebiteur.getMontantDu());

        DebiteurEntity secondDebiteur = savedDebiteurs.get(1);
        assertEquals("Jane Smith", secondDebiteur.getDebiteur());
        assertEquals("XYZ Ltd", secondDebiteur.getPromoteur());
        assertEquals("123456", secondDebiteur.getNumeroIFU());
        assertEquals(2500, secondDebiteur.getMontantDu());
    }

    @Test
    void importCSV_EmptyFile_ReturnsBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.csv",
            "text/csv",
            new byte[0]
        );

        mockMvc.perform(multipart("/debiteurs/import")
                .file(emptyFile))
            .andExpect(status().isBadRequest());

        assertEquals(0, debiteurRepository.count());
    }


    @Test
    void updatesDebiteur() throws Exception {
        var debiteur = debiteurRepository.save(
            DebiteurEntity.builder()
                .debiteur("John Doe")
                .promoteur("ABC Corp")
                .numeroIFU("123456789")
                .numeroImmatriculation("987654321")
                .registreCommerce("RC12345")
                .contacts("john.doe@example.com")
                .dateNaissance("1985-01-01")
                .numeroCNIB("CNIB12345")
                .numeroCheque("CHK98765")
                .montantDu(10000.50)
                .build()
        );

        mockMvc.perform(put("/debiteurs/" + debiteur.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(DebiteurEntity.builder()
                    .debiteur("John Doe updated")
                    .promoteur("ABC Corp")
                    .numeroIFU("123456789")
                    .numeroImmatriculation("987654321")
                    .registreCommerce("RC12345")
                    .contacts("john.doe@example.com")
                    .dateNaissance("1985-01-01")
                    .numeroCNIB("CNIB12345")
                    .numeroCheque("CHK98765")
                    .montantDu(11000.50)
                    .build()))
            )
            .andExpect(status().isOk());

        var updatedDebiteur = debiteurRepository.findById(debiteur.getId()).get();

        assertEquals("John Doe updated", updatedDebiteur.getDebiteur());
        assertEquals(11000.50, updatedDebiteur.getMontantDu());
    }

    @Test
    void createsDebiteur() throws Exception {

        mockMvc.perform(post("/debiteurs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(DebiteurEntity.builder()
                    .debiteur("John Doe")
                    .promoteur("ABC Corp")
                    .numeroIFU("123456789")
                    .numeroImmatriculation("987654321")
                    .registreCommerce("RC12345")
                    .contacts("john.doe@example.com")
                    .dateNaissance("1985-01-01")
                    .numeroCNIB("CNIB12345")
                    .numeroCheque("CHK98765")
                    .montantDu(11000.50)
                    .build()))
            )
            .andExpect(status().isOk());

        var updatedDebiteur = debiteurRepository.findByNumeroIFU("123456789").get();

        assertEquals("John Doe", updatedDebiteur.getDebiteur());
        assertEquals(11000.50, updatedDebiteur.getMontantDu());
    }


    @Test
    @Disabled
    void importCSV_InvalidFormat_ReturnsError() throws Exception {
        String invalidCsvContent = "Débiteur,Promoteur\nJohn Doe,ABC Corp";

        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "invalid.csv",
            "text/csv",
            invalidCsvContent.getBytes()
        );

        mockMvc.perform(multipart("/debiteurs/import")
                .file(invalidFile))
            .andExpect(status().isInternalServerError());

        assertEquals(0, debiteurRepository.count());
    }

    @Test
    @Disabled
    void importCSV_InvalidAmount_ReturnsError() throws Exception {
        String invalidAmountCsv = "Débiteur,Promoteur,Numero IFU,Numero d'immatriculation,Registre de commerce,Contacts,Date de naissance,Numéro CNIB,Numéro de chèque,Montant du\n" +
            "John Doe,ABC Corp,123456,IMM789,RC001,+1234567890,1990-01-01,CNIB001,CHQ123,-1000.50";

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "invalid_amount.csv",
            "text/csv",
            invalidAmountCsv.getBytes()
        );

        mockMvc.perform(multipart("/debiteurs/import")
                .file(file))
            .andExpect(status().isInternalServerError());

        assertEquals(0, debiteurRepository.count());
    }
}
