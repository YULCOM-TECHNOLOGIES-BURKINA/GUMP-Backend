package com.yulcomtechnologies.tresorms.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateParamsActeDto {
    private Long id;
    private String value;
}