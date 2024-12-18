package com.yulcomtechnologies.tresorms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDto {
    private String name;
    private String ifu;
    private String address;
    private String phone;
    private String postalAddress;
    private String location;
}
