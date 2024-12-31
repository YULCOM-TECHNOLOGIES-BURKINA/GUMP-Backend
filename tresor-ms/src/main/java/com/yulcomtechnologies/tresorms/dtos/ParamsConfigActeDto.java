package com.yulcomtechnologies.tresorms.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParamsConfigActeDto {
    private Long id;
    private String param;
    private String labelle;
    private String value;
}