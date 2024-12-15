package com.yulcomtechnologies.anpems.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponseDto {
    private String message;
    private Object data;
}
