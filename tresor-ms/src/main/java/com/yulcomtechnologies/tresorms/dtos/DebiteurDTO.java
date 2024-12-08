package com.yulcomtechnologies.tresorms.dtos;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebiteurDTO {
    @CsvBindByPosition(position = 0)
    private String debiteur;

    @CsvBindByPosition(position = 1)
    private String promoteur;

    @CsvBindByPosition(position = 2)
    private String numeroIFU;

    @CsvBindByPosition(position = 3)
    private String numeroImmatriculation;

    @CsvBindByPosition(position = 4)
    private String registreCommerce;

    @CsvBindByPosition(position = 5)
    private String contacts;

    @CsvBindByPosition(position = 6)
    private String dateNaissance;

    @CsvBindByPosition(position = 7)
    private String numeroCNIB;

    @CsvBindByPosition(position = 8)
    private String numeroCheque;

    @CsvBindByPosition(position = 9)
    private Double montantDu;
}
