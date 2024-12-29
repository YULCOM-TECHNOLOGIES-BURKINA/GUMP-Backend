package com.yulcomtechnologies.drtssms.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttestationConfigDto {
    private Long id;
    private String logo;
    private String icone;
    private String title;
    private String code;
    private String description;
    private List<ParamsConfigActeDto> acteConfig;


}
