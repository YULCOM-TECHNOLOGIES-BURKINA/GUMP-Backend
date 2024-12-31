package com.yulcomtechnologies.tresorms.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAttestationConfigDto {
    private Long id;
    private String logo;
    private String icone;
    private String title;
    private String description;


}
