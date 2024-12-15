package com.yulcomtechnologies.usersms.dtos;

import lombok.Data;

@Data
public class CompanyDto {
    private String name;
    private String ifu;
    private String address;
    private String rccm;
    private String statutDocumentPath;
    private String cnibDocumentPath;
    private String postalAddress;
    private String phone;
    private String location;
}
