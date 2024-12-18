package com.yulcomtechnologies.usersms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String address;
    private String location;
    private String postalAddress;
    private String phone;
    private String representantLastname;
    private String representantFirstname;
    private String representantPhone;
    private String region;
}
