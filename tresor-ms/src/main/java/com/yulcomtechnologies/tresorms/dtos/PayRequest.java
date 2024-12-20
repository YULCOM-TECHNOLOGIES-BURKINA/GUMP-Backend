package com.yulcomtechnologies.tresorms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayRequest {
    @NotBlank
    @NotNull
    private String requestType;

    @NotNull
    @NotBlank
    private String callbackUrl;
}
