package com.yulcomtechnologies.justicems.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private String name;
    private String ifu;
    private String address;
}